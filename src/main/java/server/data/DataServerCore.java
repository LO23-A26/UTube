package server.data;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceCommServerCallsDataServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Core business logic and in-memory data store for the Data Server.
 *
 * @author Jean Van Gysel
 * @version 1.0
 */
public class DataServerCore {

    private List<UserLight> user_list;
    private List<VideoLight> video_list;

    private CommServerCallsDataServerImpl impl_interface_for_CS;

    public DataServerCore() {
        this.user_list = new ArrayList<>();
        this.video_list = new ArrayList<>();
    }

    public ArrayList<Object> registerUserAndGetIndex(UserLight new_user, Set<VideoLight> new_videos) {
        if (new_user != null) {
            UUID user_id = new_user.getUuid();

            // If user was already connected, remove old profile
            for (int i = 0; i < user_list.size(); i++) {
                if (user_list.get(i).getUuid().equals(user_id)) {
                    user_list.remove(i);
                    break;
                }
            }

            // Remove previous videos from this user
            for (int i = video_list.size() - 1; i >= 0; i--) {
                VideoLight v = video_list.get(i);
                if (v.getOwner() != null && v.getOwner().getUuid() != null && v.getOwner().getUuid().equals(user_id)) {
                    video_list.remove(i);
                }
            }

            user_list.add(new_user);
        }

        if (new_videos != null) {
            video_list.addAll(new_videos);
        }

        ArrayList<Object> index = new ArrayList<>();
        index.add(user_list);
        index.add(video_list);
        return index;
    }

    public void removeUser(UUID user_id) {
        if (user_id == null) {
            return;
        }

        for (int i = 0; i < user_list.size(); i++) {
            if (user_id.equals(user_list.get(i).getUuid())) {
                user_list.remove(i);
                break;
            }
        }

        for (int i = video_list.size() - 1; i >= 0; i--) {
            VideoLight v = video_list.get(i);
            if (v.getOwner() != null && user_id.equals(v.getOwner().getUuid())) {
                video_list.remove(i);
            }
        }
    }

    public void removeUser(UserLight user_profile, List<VideoLight> video_list) {
        if (user_profile != null && user_profile.getUuid() != null) {
            removeUser(user_profile.getUuid());
        }
    }


    public List<UserLight> getUserList() {
        return user_list;
    }

    public void setUserList(List<UserLight> user_list) {
        this.user_list = user_list;
    }

    public List<VideoLight> getVideoList() {
        return video_list;
    }

    public void setVideoList(List<VideoLight> video_list) {
        this.video_list = video_list;
    }

    public CommServerCallsDataServerImpl getImplInterfaceForCS() {
        return impl_interface_for_CS;
    }

    public void setImplInterfaceForCS(CommServerCallsDataServerImpl impl_interface_for_CS) {
        this.impl_interface_for_CS = impl_interface_for_CS;
    }

    public InterfaceCommServerCallsDataServer getInterfaceForCS() {
        return impl_interface_for_CS;
    }
}
