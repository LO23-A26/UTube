package client.data;

import common.dataclasses.AccessRight;
import common.dataclasses.ContactList;
import common.dataclasses.UserLight;
import common.dataclasses.UserFull;
import common.dataclasses.VideoLight;
import common.dataclasses.Index;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.ArrayList;

/**
 * Implementation of the IHM Main calls Dataclient interface
 *
 * @author Lucas Dompnier, Jean Van Gysel
 * @version 2.0
 */
public class IhmMainCallsDataClientImpl implements InterfaceIhmMainCallsDataClient {

    private final DataClientCore data_client_core;

    public IhmMainCallsDataClientImpl(DataClientCore data_client_core) {
        this.data_client_core = data_client_core;
    }



    @Override
    public UserLight getUser(){
        return data_client_core.getUser().getSharedInfo();
    }

    /**
     * Exports profile data to a specified path.
     *
     * @param destination_path The string path where data should be exported.
     */
    @Override
    public void exportProfileData(String destination_path) {
        data_client_core.getProfileSaver().exportProfile(destination_path, data_client_core.getUser());
    }

/**
     * Imports profile data from a specified path.
     *
     * @param source_path The string path from where data should be imported.
     */
    @Override
    public void importProfileData(String source_path) {
        UserFull user = data_client_core.getProfileSaver().importProfile(source_path);
        data_client_core.setUser(user);
    }

    /**
     * Saves a local user profile.
     *
     * @param profile The Data Transfer Object for the user.
     */
    @Override
    public void saveLocalProfile(UserFull profile){
        data_client_core.getProfileSaver().saveProfile(profile);
    }

    /**
     * Updates an existing local user profile.
     *
     * @param profile The Data Transfer Object for the user.
     */
    @Override
    public void updateLocalProfile(UserFull profile){
        data_client_core.getProfileSaver().saveProfile(profile);
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
     * @param pseudo The user's pseudo.
     * @param password     The user's password.
     * @param ip_address   The IP address for the connection.
     * @param port         The port number.
     */
    @Override
    public boolean requestConnection(String pseudo, String password, String ip_address, int port) {
        if (data_client_core.getProfileSaver().restoreProfile() == null || data_client_core.getProfileSaver().restoreProfile().isEmpty()){
            throw new AuthentificationFailed("requestConnection - Error while restoring profiles.");
        }
        boolean found = false;
        List <VideoLight> video_list = null;
        for (UserFull u : data_client_core.getProfileSaver().restoreProfile()) {
            if (u.getPassword().equals(password) && u.getSharedInfo().getPseudo().equals(pseudo)) {
                found = true;
                data_client_core.setUser(u);
                video_list = new ArrayList<>(u.getSharedVideos());
            }
        }
        if (found) {
            data_client_core.getInterfaceFromComm().sendProfileInfo(ip_address, port, video_list, pseudo);
            return true;
        }
        else {
            throw new AuthentificationFailed("requestConnection - Information don't match.");
        }
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
        return data_client_core.getConnectedUser();
    }

    @Override
    public Index getLocalIndex() {
        return data_client_core.getIndexLocal();
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
     */
    @Override
    public void requestRemoteProfile(UserLight owner_profile) {
        data_client_core.getInterfaceFromComm().requestFetchRemoteProfile(owner_profile);
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
        if (data_client_core != null) {
            return data_client_core.searchSharedVideos(title, creator, tags);
        }
        return new ArrayList<>();
    }

    public DataClientCore getDataClientCore() {
        return data_client_core;
    }

}

