package common.interfaces.IHMMain;

import java.util.UUID;

/**
 * Interface for Communication to call IHM Main.
 *
 * @author IHM-Main
 * @version 1.0
 */
public interface InterfaceCommCallsIHMMain {

    /**
     * Displays the complete profile of another user.
     *
     * @param heavy_profile complete profile of the searched remote user
     */
    public void displayRemoteProfile(UserFull heavy_profile);

    /**
     * Removes a deleted video from the IHM Main display.
     *
     * @param video_id identifier of the deleted video to remove
     */
    public void removeVideoFromList(UUID video_id);
}