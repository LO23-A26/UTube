package client.ihmmain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import javafx.application.Platform;

/** Receives Data notifications without implementing persistence or network behavior. */
public final class DataCallsIHMMainImpl implements InterfaceDataCallsIHMMain {
    private final IHMMainManager manager;

    public DataCallsIHMMainImpl(IHMMainManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    private static <T> List<T> snapshot(Collection<T> values) {
        return values == null ? List.of() : new ArrayList<>(values);
    }

    @Override public void notifyNewUser(UserLight user, List<VideoLight> videos) {
        List<UserLight> users_copy = user == null ? List.of() : List.of(user);
        List<VideoLight> videos_copy = snapshot(videos);
        Platform.runLater(() -> {
            manager.addUsers(users_copy);
            manager.addVideos(videos_copy);
        });
    }

    @Override public void refreshFullDisplay(List<UserLight> users, List<VideoLight> videos) {
        // Same behaviour for users and videos: added to the display (see addVideos), never replaced.
        List<UserLight> users_copy = snapshot(users);
        List<VideoLight> videos_copy = snapshot(videos);
        Platform.runLater(() -> {
            manager.addUsers(users_copy);
            manager.addVideos(videos_copy);
        });
    }

    @Override public void refreshUserList(UUID userId) { // user is the disconnected user
        if (userId == null) return;
        Platform.runLater(() -> {
            manager.removeUser(userId);
            manager.removeUserVideos(userId);
        });
    }

    @Override public void displayNewVideo(VideoLight video) {
        Platform.runLater(() -> manager.addVideos(video == null ? List.of() : List.of(video)));
    }

    @Override public void refreshSharedVideos(Set<VideoLight> videos) { //The set contains all the catalog held by data
        List<VideoLight> copy = snapshot(videos);
        Platform.runLater(() -> manager.replaceVideos(copy)); //As it replaces all the videos by the current list held by data there's no problem as long as data uses FIFO
    }
}
