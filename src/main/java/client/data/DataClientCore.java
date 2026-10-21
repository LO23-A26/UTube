package client.data;

import common.dataclasses.ContactList;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;

import java.util.Set;

/**
 * @author Lucas Dompnier
 * @version 1.0
 */
public class DataClientCore {

    private UserFull user;
    private Set<VideoLight> list_video;
    private Set<UserLight> connected_user;
    private ProfileSaver profileSaver;
    private ContactList contact_lists;
    private IhmMainCallsDataClientImpl impl_interface_for_ICM;

    public IhmMainCallsDataClientImpl getImplInterfaceForICM() {
        return impl_interface_for_ICM;
    }

    public void setImplInterfaceForICM(IhmMainCallsDataClientImpl impl_interface_for_ICM) {
        this.impl_interface_for_ICM = impl_interface_for_ICM;
    }

    public UserFull getUser() {
        return user;
    }

    public void setUser(UserFull user) {
        this.user = user;
    }

    public Set<VideoLight> getListVideo() {
        return list_video;
    }

    public void setListVideo(Set<VideoLight> list_video) {
        this.list_video = list_video;
    }

    public Set<UserLight> getConnectedUser() {
        return connected_user;
    }

    public void setConnectedUser(Set<UserLight> connected_user) {
        this.connected_user = connected_user;
    }

    public ProfileSaver getProfileSaver() {
        return profileSaver;
    }

    public void setProfileSaver(ProfileSaver profileSaver) {
        this.profileSaver = profileSaver;
    }

    public ContactList getContactLists() {
        return contact_lists;
    }

    public void setContactLists(ContactList contact_lists) {
        this.contact_lists = contact_lists;
    }
}
