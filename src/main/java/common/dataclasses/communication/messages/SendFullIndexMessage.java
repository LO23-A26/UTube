package common.dataclasses.communication.messages;

import java.util.ArrayList;

import client.communication.CommClientController;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/**
 * Sent to a new user when they connect to the server, containing the full index of users and videos.
 */
public class SendFullIndexMessage implements ClientMessage {
    private final UserLight new_user;
    private final ArrayList<Object> index;

    public SendFullIndexMessage(UserLight new_user, ArrayList<Object> index) {
        this.new_user = new_user;
        this.index = index;
    }

    /**
     * Handles the message by 
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        // TODO : replace the index by a proper index object
        //controller.interfaceCommDataClient.saveFullIndex(index.videos, index.users); 
        throw new UnsupportedOperationException("Unimplemented method 'handle' in SendFullIndexMessage");
    }
}