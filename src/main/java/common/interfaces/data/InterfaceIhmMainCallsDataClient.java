package common.interfaces.data;

import common.dataclasses.*;
import java.util.UUID;
import java.util.List;
import java.util.Set;


/**
 * Interface for Main IHM calls to the data client.
 * Handles profile management, contacts, and general connections.
 */
public interface InterfaceIhmMainCallsDataClient {

    /**
     * Exports profile data to a specified path.
     *
     * @param destination_path The string path where data should be exported.
     */
    public void exportProfileData(String destination_path);

    /**
     * Imports profile data from a specified path.
     *
     * @param source_path The string path from where data should be imported.
     */
    public void importProfileData(String source_path);

    /**
     * Saves a local user profile.
     *
     * @param profile The Data Transfer Object for the user.
     */
    public void saveLocalProfile(UserLight profile);

    /**
     * Updates an existing local user profile.
     *
     * @param profile The Data Transfer Object for the user.
     */
    public void updateLocalProfile(UserLight profile);

    /**
     * Deletes a local video.
     *
     * @param video_id The UUID of the video to delete.
     */
    public void deleteLocalVideo(UUID video_id);

    /**
     * Requests a connection for a user.
     *
     * @param user_profile The lightweight user profile.
     * @param password The user's password.
     * @param ip_address The IP address for the connection.
     * @param port The port number.
     */
    public void requestConnection(UserLight user_profile, String password, String ip_address, int port);

    /**
     * Gets all contact lists for the user.
     *
     * @return A list of ContactList objects.
     */
    public List<ContactList> getContactLists();


    /**
     * Creates a new contact list.
     *
     * @param name The name of the new contact list.
     * @param right The access right associated with this list.
     * @return The newly created ContactList object, null by default.
     */
    public ContactList createContactList(String name, AccessRight right);

    /**
     * Deletes a specific contact list.
     *
     * @param list_id The UUID of the contact list to delete.
     */
    public void deleteContactList(UUID list_id);

    /**
     * Gets a list of currently connected users.
     *
     * @return A list of UserLight objects.
     */
    public List<UserLight> getConnectedUsers();

    /**
     * Gets all contacts within a specific contact list.
     *
     * @param list_id The UUID of the contact list.
     * @return A list of Contact objects.
     */
    public Set<UserLight> getContacts(UUID list_id);

    /**
     * Adds a contact to a specific list.
     *
     * @param list_id The UUID of the contact list.
     * @param user_id The UUID of the user to add as a contact.
     */
    public void addContactToList(UUID list_id, UUID user_id);

    /**
     * Removes a contact from a specific list.
     *
     * @param list_id The UUID of the contact list.
     * @param user_id The UUID of the user to remove.
     */
    public void removeContactFromList(UUID list_id, UUID user_id);

    /**
     * Sets video access rights for a specific list.
     *
     * @param list_name The name of the list.
     * @param video_id The UUID of the video.
     * @param granted Boolean indicating if access is granted.
     */
    public void setVideoAccess(String list_name, UUID video_id, boolean granted);

    /**
     * Requests the profile of a remote user.
     *
     * @param owner_profile The profile of the remote user.
     * @param requester_profile The profile of the user making the request.
     */
    public void requestRemoteProfile(UserLight owner_profile, UserLight requester_profile);

    /**
     * Searches for shared videos based on specific criteria.
     *
     * @param title The title or partial title of the video.
     * @param creator The creator of the video.
     * @param tags A set of tags associated with the video.
     * @return A list of matching VideoLight objects.
     */
    public List<VideoLight> searchSharedVideos(String title, String creator, Set<String> tags);
}
