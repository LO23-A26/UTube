package common.interfaces.data;

import common.dataclasses.*;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import java.util.Set;

/**
 * Interface for communication server calls to the data server.
 * Handles user registration, index updates, and connected client management.
 */
public interface InterfaceCommServerCallsDataServer {

    /**
     * Registers a new user along with their videos and retrieves the updated global index.
     *
     * @param new_user The lightweight profile of the new user to register.
     * @param new_videos The set of lightweight videos associated with the new user.
     * @return An ArrayList containing at index 0 the List<UserLight> (user index)
     *         and at index 1 the List<VideoLight> (video index).
     */
    public ArrayList<Object> registerUserAndGetIndex(UserLight new_user, Set<VideoLight> new_videos);
    /**
     * Removes a user from the server's records based on their unique ID.
     *
     * @param user_id The UUID of the user to be removed.
     */
    public void removeUser(UUID user_id);

    /**
     * Removes a user and purges their videos (convenience overload matching sequence diagram).
     *
     * @param user_profile The profile of the user to disconnect.
     * @param video_list Ignored as videos are managed internally by the Data Server.
     */
    default void removeUser(UserLight user_profile, List<VideoLight> video_list) {
        if (user_profile != null && user_profile.getUuid() != null) {
            removeUser(user_profile.getUuid());
        }
    }

    /**
     * Deletes a specific video index from the server.
     *
     * @param video_id The UUID of the video index to delete.
     */
    public void deleteIndex(UUID video_id);

    /**
     * Updates an existing video index with new information.
     *
     * @param video_info The lightweight video object containing the updated data.
     */
    public void updateIndex(VideoLight video_info);

    /**
     * Retrieves the list of all currently connected clients.
     *
     * @return A list of connected UserLight profiles, empty by default.
     */
    public List<UserLight> getConnectedClients();
}
