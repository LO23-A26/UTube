package common.dataclasses.communication.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import client.communication.RemoteClient;
import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

public class ConnectionRequestMessage implements ServerMessage {
    private final UserLight new_user;
    private final Set<VideoLight> new_user_videos;
    private final int new_user_port;

    /**
     * Constructor
     */
    public ConnectionRequestMessage(UserLight new_user, int new_user_port, Set<VideoLight> new_user_videos) {
        this.new_user = new_user;
        this.new_user_port = new_user_port;
        this.new_user_videos = new_user_videos;
    }

    /**
     * Handles the message by registering the new user and their videos on the server side.
     *
     * @param controller The client controller.
     * @param sender The active client sending the request.
     */
    @Override
    public void handle(CommServerController controller, ActiveClient sender) {

        // the IP address of the new client is the one the server sees on the connection
        String new_user_ip = sender.getIpAddress();

        try {
            InetAddress ip = InetAddress.getByName(new_user_ip);
            sender.setRemoteClient(new RemoteClient(ip, new_user_port, new_user.getUuid()));
        } catch (UnknownHostException ex) {
            System.getLogger(ConnectionRequestMessage.class.getName()).log(System.Logger.Level.WARNING,
                    "IP address not valid " + new_user.getUuid() + " : " + new_user_ip);
        }

        Index index = controller.interfaceCommServerCallsDataServer.registerUserAndGetIndexStructure(new_user, new_user_videos);
        // send the index (and the address of the other clients) to the new user
        controller.sendFullIndex(sender, index);
        // send the new infos to all
        controller.broadcastNewUserNotification(sender, new_user, new_user_ip, new_user_port, new_user_videos);
    }
    
}
