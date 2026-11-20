package client.ihmmain;

import common.dataclasses.UserLight;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

    @Test void userCallbacksRefreshUsersOnJavafxButVideoCallbacksDoNotRefreshUsers() throws Exception {
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
        callbacks.refreshUserList(new UserLight("bob"));
        callbacks.refreshUserList(null); // no user to remove: nothing to refresh
        callbacks.displayNewVideo(null);
        callbacks.refreshSharedVideos(Set.of());
        fx(() -> {
            assertEquals(3, calls.get());
            assertTrue(allOnFx.get());
            return null;
        });
    }

    @Test void usersComeFromTheNotificationsBeforeAndAfterMainViewLoads() throws Exception {
        IHMMainManager manager = new IHMMainManager(); // no Data interface: the panel must not need it
        DataCallsIHMMainImpl callbacks = new DataCallsIHMMainImpl(manager);
        UserLight alice = new UserLight("alice");
        UserFull bob_profile = new UserFull("bob", "secret", "Bob", "Test", null);
        UserLight bob = bob_profile.getSharedInfo();
        UserLight carol = new UserLight("carol");

        // A notification received before the main view is kept and displayed once the view loads.
        callbacks.refreshFullDisplay(List.of(alice), List.of());
        Stage stage = fx(() -> {
            Stage window = new Stage();
            manager.start(window);
            manager.changeView(IHMMainManager.MAIN_VIEW);
            window.hide();
            assertEquals(List.of("alice"), displayedUsers(window));
            return window;
        });
        try {
            callbacks.notifyNewUser(bob, List.of());
            callbacks.notifyNewUser(bob, List.of()); // same user twice: no duplicate row
            fx(() -> { assertEquals(List.of("alice", "bob"), displayedUsers(stage)); return null; });
            // refreshFullDisplay adds users, like videos: users already displayed are kept.
            callbacks.refreshFullDisplay(List.of(carol), List.of());
            fx(() -> { assertEquals(List.of("alice", "bob", "carol"), displayedUsers(stage)); return null; });
            callbacks.refreshUserList(bob);
            fx(() -> { assertEquals(List.of("alice", "carol"), displayedUsers(stage)); return null; });
            fx(() -> {
                manager.changeView(IHMMainManager.LOGIN_VIEW);
                assertTrue(manager.getDisplayedUsers().isEmpty());
                return null;
            });
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
