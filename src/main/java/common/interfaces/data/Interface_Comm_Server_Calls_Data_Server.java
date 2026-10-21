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
public interface Interface_Comm_Server_Calls_Data_Server {

    /**
     * Registers a new user along with their videos and retrieves the updated global index.
     *
     * @param new_user The lightweight profile of the new user to register.
     * @param new_videos The set of lightweight videos associated with the new user.
     * @return An object containing lists of users and videos.
     */
    public ArrayList<Object> registerUserAndGetIndex(UserLight new_user, Set<VideoLight> new_videos);
    /**
     * Removes a user from the server's records based on their unique ID.
     *
     * @param user_id The UUID of the user to be removed.
     */
    public void removeUser(UUID user_id);

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
