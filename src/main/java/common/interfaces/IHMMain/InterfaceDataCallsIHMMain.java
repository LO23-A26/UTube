package common.interfaces.IHMMain;

import java.util.List;
import java.util.Set;

/**
 * Interface for Data to call IHM Main.
 *
 * @author IHM-Main
 * @version 1.0
 */

public interface InterfaceDataCallsIHMMain {

    /**
     * Displays an authentication error.
     *
     * @param authentication_failed authentication error due to connexion failure
     */
    public void raiseException(AuthenticationFailed authentication_failed);

    /**
     * Notifies IHM Main that a new user is connected.
     *
     * @param new_user the new connected user
     * @param new_video videos of the new connected user
     */
    public void notifyNewUser(UserLight new_user, List<VideoLight> new_videos);

    /**
     * Refreshes the complete user and video display.
     *
     * @param user_list list of users
     * @param video_list list of videos
     */
    public void refreshFullDisplay(
            List<UserLight> user_list,
            List<VideoLight> video_list
    );

    /**
     * Refreshes the displayed user list.
     *
     * @param user_profile user profile of the disconnected user
     */
    public void refreshUserList(UserFull user_profile);

    /**
     * Displays a newly available video.
     *
     * @param video_info information about the new video available
     */
    public void displayNewVideo(VideoLight video_info);

    /**
     * Refreshes the displayed shared videos.
     *
     * @param videos set of currently shared videos
     */
    public void refreshSharedVideos(Set<VideoLight> videos);

}