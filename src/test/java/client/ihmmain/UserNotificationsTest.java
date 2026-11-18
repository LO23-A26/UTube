package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class UserNotificationsTest {
    @BeforeAll static void toolkit() throws Exception { LoginConnectionTest.startToolkit(); }

    private static <T> T fx(Callable<T> action) throws Exception {
        FutureTask<T> future = new FutureTask<>(action);
        Platform.runLater(future);
        return future.get(10, TimeUnit.SECONDS);
    }

    @Test void userCallbacksAreQueuedOnJavafxAndVideoCallbacksDoNothing() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        AtomicReference<Boolean> allOnFx = new AtomicReference<>(true);
        DataCallsIHMMainImpl callbacks = new DataCallsIHMMainImpl(new IHMMainManager() {
            @Override void refreshUsers() {
                allOnFx.updateAndGet(previous -> previous && Platform.isFxApplicationThread());
                calls.incrementAndGet();
            }
        });
        assertFalse(Platform.isFxApplicationThread());
        callbacks.notifyNewUser(new UserLight("alice"), List.of());
        callbacks.refreshFullDisplay(List.of(), List.of());
        callbacks.refreshUserList(null);
        callbacks.displayNewVideo(null);
        callbacks.refreshSharedVideos(Set.of());
        fx(() -> {
            assertEquals(3, calls.get());
            assertTrue(allOnFx.get());
            return null;
        });
    }

    @Test void notificationsRefreshDataUsersBeforeAndAfterMainViewLoads() throws Exception {
        IHMMainManager manager = new IHMMainManager();
        AtomicReference<List<UserLight>> users = new AtomicReference<>(List.of(new UserLight("alice")));
        InterfaceIhmMainCallsDataClient data = (InterfaceIhmMainCallsDataClient) Proxy.newProxyInstance(
                InterfaceIhmMainCallsDataClient.class.getClassLoader(),
                new Class<?>[]{InterfaceIhmMainCallsDataClient.class}, (proxy, method, args) -> {
                    assertEquals("getConnectedUsers", method.getName());
                    assertTrue(Platform.isFxApplicationThread());
                    return users.get();
                });
        manager.setIData(data);
        DataCallsIHMMainImpl callbacks = new DataCallsIHMMainImpl(manager);
        // A network notification before the main view must be harmless.
        callbacks.refreshFullDisplay(List.of(), List.of());
        Stage stage = fx(() -> {
            Stage window = new Stage();
            manager.start(window);
            manager.changeView(IHMMainManager.MAIN_VIEW);
            window.hide();
            assertEquals(List.of("alice"), displayedUsers(window));
            return window;
        });
        try {
            // The display reads Data, not the notification arguments, and replaces existing rows.
            users.set(List.of(new UserLight("bob"), new UserLight("carol")));
            callbacks.notifyNewUser(new UserLight("not-the-data-source"), List.of());
            fx(() -> { assertEquals(List.of("bob", "carol"), displayedUsers(stage)); return null; });
            callbacks.refreshFullDisplay(List.of(), List.of());
            fx(() -> { assertEquals(List.of("bob", "carol"), displayedUsers(stage)); return null; });
            users.set(List.of());
            callbacks.refreshUserList(new UserFull("bob", "secret", "Bob", "Test", null));
            fx(() -> { assertTrue(displayedUsers(stage).isEmpty()); return null; });
            fx(() -> { manager.changeView(IHMMainManager.LOGIN_VIEW); return null; });
            callbacks.notifyNewUser(new UserLight("late"), List.of());
            fx(() -> { assertNotNull(stage.getScene().lookup("#connectButton")); return null; });
        } finally {
            fx(() -> { stage.close(); return null; });
        }
    }

    private static List<String> displayedUsers(Stage window) {
        window.getScene().getRoot().applyCss();
        window.getScene().getRoot().layout();
        VBox container = (VBox) window.getScene().lookup("#users_container");
        return container.getChildren().stream().filter(HBox.class::isInstance)
                .map(HBox.class::cast).map(row -> ((Label) row.getChildren().get(1)).getText()).toList();
    }
}
