package client.data;

import common.dataclasses.AccessRight;
import common.dataclasses.ContactList;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the IHM Main calls Dataclient interface
 *
 * @author Lucas Dompnier
 * @version 1.0
 */
public class IhmMainCallsDataClientImpl implements InterfaceIhmMainCallsDataClient {

    private DataClientCore data_client_core;

    /**
     * Exports profile data to a specified path.
     *
     * @param destination_path The string path where data should be exported.
     */
    @Override
    public void exportProfileData(String destination_path) {

    }

    /**
     * Imports profile data from a specified path.
     *
     * @param source_path The string path from where data should be imported.
     */
    @Override
    public void importProfileData(String source_path) {

    }

    /**
     * Saves a local user profile.
     *
     * @param profile The Data Transfer Object for the user.
     */
    @Override
    public void saveLocalProfile(UserLight profile){

    }

    /**
     * Updates an existing local user profile.
     *
     * @param profile The Data Transfer Object for the user.
     */
    @Override
    public void updateLocalProfile(UserLight profile){

    }

    /**
     * Deletes a local video.
     *
     * @param video_id The UUID of the video to delete.
     */
    @Override
    public void deleteLocalVideo(UUID video_id) {

    }

    /**
     * Requests a connection for a user.
     *
     * @param user_profile The lightweight user profile.
     * @param password     The user's password.
     * @param ip_address   The IP address for the connection.
     * @param port         The port number.
     */
    @Override
    public void requestConnection(String pseudo, String password, String ip_address, int port) {
        if (data_client_core.getProfileSaver().restore_profile() == null || data_client_core.getProfileSaver().restore_profile().size() == 0){
            System.out.println("requestConnection - Error while restoring profiles.");
            return
        }
        UserFull profile = null;
        Set<VideoLight> video_list = null;
        for (UserFull u : data_client_core.getProfileSaver().restore_profile()) {
            if (u.getPassword().equals(password) && u.shared_info.getPseudo().equals(pseudo)) {
                profile = new UserFull(u);
                video_list = new HashSet<VideoLight>();
                for (VideoLight v : u.getSharedVideos()){
                    video_list.add(v);
                }
            }
        }
        if (profile) {
            InterfaceDataClientCallsCommClient.sendProfileInfo(ip_address, port, video_list, pseudo);
        }
        else {
            System.out.println("requestConnection - Information don't match.");
        }
        return
    }

    /**
     * Gets all contact lists for the user.
     *
     * @return A list of ContactList objects.
     */
    @Override
    public List<ContactList> getContactLists() {
        return List.of();
    }

    /**
     * Creates a new contact list.
     *
     * @param name  The name of the new contact list.
     * @param right The access right associated with this list.
     * @return The newly created ContactList object, null by default.
     */
    @Override
    public ContactList createContactList(String name, AccessRight right) {
        return null;
    }

    /**
     * Deletes a specific contact list.
     *
     * @param list_id The UUID of the contact list to delete.
     */
    @Override
    public void deleteContactList(UUID list_id) {

    }

    /**
     * Gets a list of currently connected users.
     *
     * @return A list of UserLight objects.
     */
    @Override
    public List<UserLight> getConnectedUsers() {
        return List.of();
    }

    /**
     * Gets all contacts within a specific contact list.
     *
     * @param list_id The UUID of the contact list.
     * @return A list of Contact objects.
     */
    @Override
    public Set<UserLight> getContacts(UUID list_id) {
        return Set.of();
    }

    /**
     * Adds a contact to a specific list.
     *
     * @param list_id The UUID of the contact list.
     * @param user_id The UUID of the user to add as a contact.
     */
    @Override
    public void addContactToList(UUID list_id, UUID user_id) {

    }

    /**
     * Removes a contact from a specific list.
     *
     * @param list_id The UUID of the contact list.
     * @param user_id The UUID of the user to remove.
     */
    @Override
    public void removeContactFromList(UUID list_id, UUID user_id) {

    }

    /**
     * Sets video access rights for a specific list.
     *
     * @param list_name The name of the list.
     * @param video_id  The UUID of the video.
     * @param granted   Boolean indicating if access is granted.
     */
    @Override
    public void setVideoAccess(String list_name, UUID video_id, boolean granted) {

    }

    /**
     * Requests the profile of a remote user.
     *
     * @param owner_profile     The profile of the remote user.
     * @param requester_profile The profile of the user making the request.
     */
    @Override
    public void requestRemoteProfile(UserLight owner_profile, UserLight requester_profile) {

    }

    /**
     * Searches for shared videos based on specific criteria.
     *
     * @param title   The title or partial title of the video.
     * @param creator The creator of the video.
     * @param tags    A set of tags associated with the video.
     * @return A list of matching VideoLight objects.
     */
    @Override
    public List<VideoLight> searchSharedVideos(String title, String creator, Set<String> tags) {
        return List.of();
    }

    public DataClientCore getDataClientCore() {
        return data_client_core;
    }

    public void setDataClientCore(DataClientCore data_client_core) {
        this.data_client_core = data_client_core;
    }
}
