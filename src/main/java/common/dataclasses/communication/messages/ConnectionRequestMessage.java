package common.dataclasses.communication.messages;
import java.util.Set;

import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.PeerAddress;
import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

public class ConnectionRequestMessage implements ServerMessage {
    private final UserLight new_user;
    private final Set<VideoLight> new_user_videos;
    private final String new_user_ip; // will be used in the new user notification
    private final int new_user_port;

    /**
     * Constructor
     */
    public ConnectionRequestMessage(UserLight new_user, String new_user_ip, int new_user_port, Set<VideoLight> new_user_videos) {
        this.new_user = new_user;
        this.new_user_ip = new_user_ip;
        this.new_user_port = new_user_port;
        this.new_user_videos = new_user_videos;
    }

    /**
     * Handles the message by registering the new user and their videos on the server side.
     *
     * @param controller   The client controller.
     * @param sender
     */
    @Override
    public void handle(CommServerController controller, ActiveClient sender) {
        // remember where this client listens for the other clients
        sender.setPeerAddress(new PeerAddress(new_user.getUuid(), new_user_ip, new_user_port));
        System.getLogger(ConnectionRequestMessage.class.getName()).log(System.Logger.Level.INFO,
                "Client " + new_user.getUuid() + " (connection " + sender.getUuid() + ") listens for the other clients on "
                + new_user_ip + ":" + new_user_port);
        Index index = controller.interfaceCommServerCallsDataServer.registerUserAndGetIndexStructure(new_user,new_user_videos);
        // send the index (and the address of the other clients) to the new user
        controller.sendFullIndex(sender, index);
        // send the new infos to all
        controller.broadcastNewUserNotification(sender, new_user, new_user_ip, new_user_port, new_user_videos);
    }
    
}
