package server.communication;

import java.util.List;
import java.util.UUID;

import common.interfaces.InterfaceData_calls_Comm;

public class CommServerController implements InterfaceData_calls_Comm {
    private ConnectionsManager connectionsManager;
    private InterfaceCommServer_calls_DataServer interfaceCommServerCallsDataServer;

    public CommServerController(ConnectionsManager connectionsManager, InterfaceCommServer_calls_DataServer interfaceCommServerCallsDataServer) {
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
}