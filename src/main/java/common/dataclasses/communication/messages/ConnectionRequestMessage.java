package common.dataclasses.communication.messages;

import java.util.Set;

import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ServerMessage;
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
     * @param controller The client controller.
     */
    @Override
    public void handle(CommServerController controller) {  
        Index index = controller.interfaceCommServerCallsDataServer.registerUserAndGetIndexStructure(this.new_user, this.new_user_videos);
        // send the new infos to all (not implemented yet)
        controller.broadcastNewUserNotification(this.new_user, new_user_ip, new_user_port, this.new_user_videos);

        // send the index to the new user
        controller.sendFullIndex(new_user, index);
    }
    
}
