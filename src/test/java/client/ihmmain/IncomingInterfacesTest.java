package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.application.Platform;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class IncomingInterfacesTest {
    @BeforeAll static void toolkit() throws Exception { LoginConnectionTest.startToolkit(); }

    private static <T> T fx(Callable<T> action) throws Exception {
        FutureTask<T> result = new FutureTask<>(action);
        Platform.runLater(result);
        return result.get(10, TimeUnit.SECONDS);
    }

    private static VideoLight video(UserLight owner, String title) {
        return new VideoLight(owner, title, owner.getPseudo(), Set.of());
    }

    @Test void videoNotificationsAreRetainedBeforeViewsAndRenderedWithoutDuplicates() throws Exception {
        IHMMainManager manager = new IHMMainManager();
        UserLight alice = new UserLight("alice");
        VideoLight first = video(alice, "First");
        List<VideoLight> initial = new ArrayList<>(List.of(first));
        // Queue from another thread while JavaFX is held, then mutate the caller's list.
        fx(() -> {
            Thread sender = new Thread(() -> manager.getInterfaceForData().refreshFullDisplay(List.of(alice), initial));
            sender.start(); sender.join(); initial.clear(); return null;
        });
        manager.getInterfaceForData().displayNewVideo(first);
        fx(() -> { assertEquals(List.of(first), manager.getDisplayedVideos()); return null; });
        Stage stage = fx(() -> {
            manager.setIData((InterfaceIhmMainCallsDataClient) Proxy.newProxyInstance(
                    InterfaceIhmMainCallsDataClient.class.getClassLoader(),
                    new Class<?>[]{InterfaceIhmMainCallsDataClient.class},
                    (proxy, method, args) -> List.of(alice)));
            Stage window = new Stage(); manager.start(window); manager.showContent(IHMMainManager.HOME_VIEW);
            window.hide(); return window;
        });
        try {
            fx(() -> {
                stage.getScene().getRoot().applyCss(); stage.getScene().getRoot().layout();
                TilePane cards = (TilePane) stage.getScene().lookup("#videos_container");
                assertEquals(1, cards.getChildren().size()); return null;
            });
            manager.getInterfaceForComm().removeVideoFromList(first.getVideoId());
            fx(() -> {
                assertTrue(manager.getDisplayedVideos().isEmpty());
                assertEquals(0, ((TilePane)stage.getScene().lookup("#videos_container")).getChildren().size());
                return null;
            });
        } finally { fx(() -> { stage.close(); return null; }); }
    }

    @Test void replacementDepartureAndLogoutUpdateOnlyPresentationState() throws Exception {
        IHMMainManager manager = new IHMMainManager();
        UserFull alice = new UserFull("alice", "secret", "Alice", "Test", null);
        VideoLight first = video(alice.getSharedInfo(), "First");
        VideoLight second = video(new UserLight("bob"), "Second");
        manager.getInterfaceForData().notifyNewUser(alice.getSharedInfo(), List.of(first));
        manager.getInterfaceForData().displayNewVideo(second);
        manager.getInterfaceForData().refreshUserList(alice);
        fx(() -> { assertEquals(List.of(second), manager.getDisplayedVideos()); return null; });
        manager.getInterfaceForData().refreshSharedVideos(Set.of(first));
        fx(() -> { assertEquals(List.of(first), manager.getDisplayedVideos()); return null; });
        manager.getInterfaceForData().refreshFullDisplay(List.of(), List.of());
        fx(() -> { assertTrue(manager.getDisplayedVideos().isEmpty()); return null; });
        manager.getInterfaceForData().displayNewVideo(first);
        fx(() -> {
            Stage stage = new Stage();
            try {
                manager.start(stage);
                manager.changeView(IHMMainManager.LOGIN_VIEW);
                assertTrue(manager.getDisplayedVideos().isEmpty());
            } finally { stage.close(); }
            return null;
        });
    }

    @Test void remoteProfileIsReadOnlyAndNeverDisplaysThePassword() throws Exception {
        IHMMainManager manager = new IHMMainManager();
        UserFull alice = new UserFull("alice", "never-display-this", "Alice", "Example", LocalDate.of(2000, 1, 2));
        manager.getInterfaceForComm().displayRemoteProfile(alice);
        try {
            fx(() -> {
                DialogPane pane = profilePane();
                assertEquals("alice", pane.getHeaderText());
                assertTrue(pane.getContentText().contains("Alice"));
                assertTrue(pane.getContentText().contains("Example"));
                assertTrue(pane.getContentText().contains("2000-01-02"));
                assertFalse(pane.getContentText().contains("never-display-this"));
                assertTrue(pane.lookupAll(".text-input").isEmpty());
                return null;
            });
            manager.getInterfaceForComm().displayRemoteProfile(null);
            fx(() -> {
                assertEquals("Profil indisponible", profilePane().getHeaderText());
                assertFalse(profilePane().getContentText().contains("Alice")); return null;
            });
        } finally {
            fx(() -> {
                List.copyOf(Window.getWindows()).stream()
                        .filter(w -> w instanceof Stage s && "Profil utilisateur".equals(s.getTitle()))
                        .forEach(Window::hide);
                return null;
            });
        }
    }

    private static DialogPane profilePane() {
        Window window = Window.getWindows().stream()
                .filter(w -> w instanceof Stage s && "Profil utilisateur".equals(s.getTitle())).findFirst().orElseThrow();
        return (DialogPane) window.getScene().getRoot();
    }
}
