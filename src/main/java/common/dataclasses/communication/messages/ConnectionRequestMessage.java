package common.dataclasses.communication.messages;

import java.util.ArrayList;
import java.util.Set;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ServerMessage;
import server.communication.CommServerController;

public class ConnectionRequestMessage implements ServerMessage {
    private final UserLight new_user;
    private final Set<VideoLight> new_user_videos;

    public ConnectionRequestMessage(UserLight new_user, Set<VideoLight> new_user_videos) {
        this.new_user = new_user;
        this.new_user_videos = new_user_videos;
    }

    /**
     * Handles the message by registering the new user and their videos on the server side.
     * @param controller The client controller.
     */
    @Override
    public void handle(CommServerController controller) {
        ArrayList<Object> index = controller.interfaceCommServerCallsDataServer.registerUserAndGetIndex(this.new_user, this.new_user_videos);
        // send the new infos to all
        controller.broadcastNewUserNotification(this.new_user, this.new_user_videos);

        // send the index to the new user
        controller.sendFullIndex(new_user, index);
    }
    
}
