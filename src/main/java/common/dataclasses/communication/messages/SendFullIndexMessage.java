package common.dataclasses.communication.messages;

import java.util.ArrayList;
import java.util.List;

import client.communication.CommClientController;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/**
 * Sent to a new user when they connect to the server, containing the full index of users and videos.
 */
public class SendFullIndexMessage implements ClientMessage {
    private final ArrayList<Object> index;

    public SendFullIndexMessage(ArrayList<Object> index) {
        this.index = index;
    }

    /**
     * Handles the message by giving the full index to the Data client.
     * The index built by the Data server is [List of UserLight, List of VideoLight].
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        List<UserLight> users = (List<UserLight>) index.get(0);
        List<VideoLight> videos = (List<VideoLight>) index.get(1);
        controller.interfaceCommDataClient.saveFullIndex(users, videos);
    }
}
