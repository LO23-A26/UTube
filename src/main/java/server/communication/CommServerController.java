package server.communication;

import java.util.List;
import java.util.UUID;

import common.interfaces.comm.InterfaceData_calls_Comm;
import common.interfaces.data.InterfaceCommServerCallsDataServer;
import common.dataclasses.*;

public class CommServerController implements InterfaceData_calls_Comm {
    private ConnectionsManager connectionsManager;
    private InterfaceCommServerCallsDataServer interfaceCommServerCallsDataServer;

    public CommServerController(ConnectionsManager connectionsManager, InterfaceCommServerCallsDataServer interfaceCommServerCallsDataServer) {
        this.connectionsManager = connectionsManager;
        this.interfaceCommServerCallsDataServer = interfaceCommServerCallsDataServer;
    }

    public void registerUserAndGetIndex(UserLight new_user, List<VideoLight> new_videos) {}

    public List<UserLight> getConnectedClients() {
        return null;
    }

    public void removeUser(UserLight user_profile, List<VideoLight> video_list) {}

    public void updateIndex(VideoLight video_info) {}

    public List<UserLight> deleteIndex(UUID video_id) {
        return null;
    }

    // interface implementation


    public void sendProfileInfo(String ip_address, int port, List<VideoLight> video_list, String pseudo) {
    }

    public void sendNewVideo(VideoLight video_info) {

    }

    public void giveDetailsRequest(UserLight requester_profile, UserLight owner_profile, UUID video) {

    }
}