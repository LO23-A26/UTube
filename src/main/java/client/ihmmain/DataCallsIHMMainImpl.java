package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import javafx.application.Platform;

import java.util.List;
import java.util.Set;

/** Routes Data notifications to the connected users panel on the JavaFX thread. */
public class DataCallsIHMMainImpl implements InterfaceDataCallsIHMMain {
    private final IHMMainManager view_manager;

    public DataCallsIHMMainImpl(IHMMainManager view_manager) {
        this.view_manager = view_manager;
    }

    @Override
    public void notifyNewUser(UserLight new_user, List<VideoLight> new_videos) {
        Platform.runLater(view_manager::refreshUsers);
    }

    @Override
    public void refreshFullDisplay(List<UserLight> user_list, List<VideoLight> video_list) {
        Platform.runLater(view_manager::refreshUsers);
    }

    @Override
    public void refreshUserList(UserFull user_profile) {
        Platform.runLater(view_manager::refreshUsers);
    }

    @Override
    public void displayNewVideo(VideoLight video_info) {
        // Video notifications are outside this delivery.
    }

    @Override
    public void refreshSharedVideos(Set<VideoLight> videos) {
        // Video notifications are outside this delivery.
    }
}
