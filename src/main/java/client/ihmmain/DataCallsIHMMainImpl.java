package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
        List<VideoLight> copy = snapshot(videos);
        Platform.runLater(() -> {
            manager.refreshUsers();
            manager.addVideos(copy);
        });
    }

    @Override public void refreshFullDisplay(List<UserLight> users, List<VideoLight> videos) {
        List<VideoLight> copy = snapshot(videos);
        Platform.runLater(() -> {
            // Data is the source of truth for connected users, as in the user-panel integration.
            manager.refreshUsers();
            manager.replaceVideos(copy);
        });
    }

    @Override public void refreshUserList(UserFull user) {
        UUID id = user == null || user.getSharedInfo() == null ? null : user.getSharedInfo().getUuid();
        Platform.runLater(() -> {
            manager.refreshUsers();
            if (id != null) manager.removeUserVideos(id);
        });
    }

    @Override public void displayNewVideo(VideoLight video) {
        Platform.runLater(() -> manager.addVideos(video == null ? List.of() : List.of(video)));
    }

    @Override public void refreshSharedVideos(Set<VideoLight> videos) {
        List<VideoLight> copy = snapshot(videos);
        Platform.runLater(() -> manager.replaceVideos(copy));
    }
}
