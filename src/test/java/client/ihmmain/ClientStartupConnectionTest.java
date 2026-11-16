package client.ihmmain;

import client.data.IhmMainCallsDataClientImpl;
import client.main.UTubeApplication;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ClientStartupConnectionTest {
    private IHMMainManager manager;
    private Parent login;
    private final AtomicInteger navigation = new AtomicInteger();

    @BeforeAll static void startToolkit() throws Exception {
        CountDownLatch ready = new CountDownLatch(1);
        Platform.startup(() -> { Platform.setImplicitExit(false); ready.countDown(); });
        assertTrue(ready.await(10, TimeUnit.SECONDS));
    }

    private static <T> T fx(Callable<T> action) throws Exception {
        FutureTask<T> result = new FutureTask<>(action);
        Platform.runLater(result);
        return result.get(10, TimeUnit.SECONDS);
    }

    @BeforeEach void loadLogin() throws Exception {
        fx(() -> {
            manager = new IHMMainManager() {
                @Override public void showContent(String path) { navigation.incrementAndGet(); }
            };
            FXMLLoader loader = new FXMLLoader(getClass().getResource(IHMMainManager.LOGIN_VIEW));
            loader.setControllerFactory(type -> {
                LoginController controller = new LoginController();
                controller.setViewManager(manager);
                return controller;
            });
            login = loader.load();
            field("pseudoField").setText("alice");
            field("passwordField").setText("secret");
            field("ipField").setText("127.0.0.1");
            field("portField").setText("8080");
            return null;
        });
    }

    private TextField field(String id) { return (TextField) login.lookup("#" + id); }
    private Button button() { return (Button) login.lookup("#connectButton"); }
    private Label message() { return (Label) login.lookup("#errorLabel"); }
    private InterfaceIhmMainCallsDataClient service(Callable<Boolean> call) {
        return (InterfaceIhmMainCallsDataClient) Proxy.newProxyInstance(
                InterfaceIhmMainCallsDataClient.class.getClassLoader(),
                new Class<?>[]{InterfaceIhmMainCallsDataClient.class}, (proxy, method, args) -> {
                    assertEquals("requestConnection", method.getName());
                    assertFalse(Platform.isFxApplicationThread());
                    assertArrayEquals(new Object[]{"alice", "secret", "127.0.0.1", 8080}, args);
                    return call.call();
                });
    }

    private void awaitCompletion() throws Exception {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (fx(() -> button().isDisabled())) {
            if (System.nanoTime() > deadline) fail("Connection request did not complete");
            Thread.sleep(10);
        }
    }

    @Test void startupUsesTheSameDataCoreThroughPublicApis() throws Exception {
        fx(() -> {
            IHMMainManager wired = UTubeApplication.createManager();
            IhmMainCallsDataClientImpl facade = assertInstanceOf(IhmMainCallsDataClientImpl.class, wired.getIData());
            assertNotNull(facade.getDataClientCore());
            assertSame(facade, facade.getDataClientCore().getImplInterfaceForICM());
            assertNotNull(facade.getDataClientCore().getProfileSaver());
            assertNotNull(facade.getDataClientCore().getInterfaceFromComm());
            assertNotNull(wired.getIComm());
            assertNull(wired.getIShare());
            return null;
        });
    }

    @Test void missingServiceIsVisibleWithoutNavigating() throws Exception {
        fx(() -> {
            button().fire();
            assertTrue(message().isVisible());
            assertTrue(message().getText().contains("indisponible"));
            assertEquals(0, navigation.get());
            return null;
        });
    }

    @Test void invalidInputDoesNotCallData() throws Exception {
        fx(() -> {
            manager.setIData(service(() -> { fail("Invalid input reached Data"); return false; }));
            field("portField").setText("0");
            button().fire();
            assertTrue(message().getText().contains("port"));
            assertFalse(button().isDisabled());
            return null;
        });
    }

    @Test void acceptedRequestRunsOffJavafxAndSuppressesDoubleClick() throws Exception {
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        AtomicInteger calls = new AtomicInteger();
        fx(() -> {
            manager.setIData(service(() -> {
                calls.incrementAndGet(); entered.countDown();
                assertTrue(release.await(5, TimeUnit.SECONDS)); return true;
            }));
            button().fire(); button().fire();
            assertTrue(button().isDisabled());
            assertTrue(field("pseudoField").isDisabled());
            assertEquals("", ((PasswordField) field("passwordField")).getText());
            return null;
        });
        try { assertTrue(entered.await(5, TimeUnit.SECONDS)); }
        finally { release.countDown(); }
        awaitCompletion();
        fx(() -> {
            assertEquals(1, calls.get());
            assertEquals(0, navigation.get());
            assertTrue(message().getText().contains("non confirmée"));
            assertFalse(field("pseudoField").isDisabled());
            return null;
        });
    }

    @Test void refusalKeepsLoginAvailable() throws Exception {
        fx(() -> { manager.setIData(service(() -> false)); button().fire(); return null; });
        awaitCompletion();
        fx(() -> {
            assertTrue(message().getText().contains("refusée"));
            assertEquals(0, navigation.get()); return null;
        });
    }

    @Test void exceptionIsVisibleAndAllowsRetry() throws Exception {
        fx(() -> {
            manager.setIData(service(() -> { throw new IllegalStateException("Service indisponible"); }));
            button().fire(); return null;
        });
        awaitCompletion();
        fx(() -> {
            assertTrue(message().getText().contains("Service indisponible"));
            assertEquals(0, navigation.get());
            manager.setIData(service(() -> false));
            field("passwordField").setText("secret"); button().fire(); return null;
        });
        awaitCompletion();
        assertTrue(fx(() -> message().getText().contains("refusée")));
    }
}
