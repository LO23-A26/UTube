package server.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.comm.InterfaceDataCallsComm;
import common.interfaces.data.InterfaceCommServerCallsDataServer;

public class CommServerController implements InterfaceDataCallsComm {
    private ConnectionsManager connectionsManager;
    public InterfaceCommServerCallsDataServer interfaceCommServerCallsDataServer;
    private NewConnectionListeningThread newConnectionListeningThread;

    public CommServerController(ConnectionsManager connectionsManager, InterfaceCommServerCallsDataServer interfaceCommServerCallsDataServer) {
        this.connectionsManager = connectionsManager;
        this.interfaceCommServerCallsDataServer = interfaceCommServerCallsDataServer;
        this.newConnectionListeningThread = new NewConnectionListeningThread(connectionsManager);
    }

    public void start() {
        newConnectionListeningThread.start();
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

    /* 
     * Broadcasts a notification (NewUserNotificationMessage) to all connected clients about a new user.
     * @param new_user The new user.
     * @param new_user_videos The videos of the new user.
     */
    public void broadcastNewUserNotification(UserLight new_user, Set<VideoLight> new_user_videos) {
        throw new UnsupportedOperationException("Unimplemented method 'broadcastNewUserNotification'");
    }

    /*
     * Sends the full index to a new user (SendFullIndexMessage).
     * @param new_user The new user.
     * @param index The full index.
     */
    public void sendFullIndex(UserLight new_user, ArrayList<Object> index) {
        throw new UnsupportedOperationException("Unimplemented method 'sendFullIndex'");
    }
}