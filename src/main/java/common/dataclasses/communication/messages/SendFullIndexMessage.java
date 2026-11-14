package common.dataclasses.communication.messages;

import java.util.List;

import client.communication.CommClientController;
import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/**
 * Sent to a new user when they connect to the server, containing the full index of users and videos.
 */
public class SendFullIndexMessage implements ClientMessage {
    private final Index index;

    public SendFullIndexMessage(Index index) {
        this.index = index;
    }

    /**
     * Handles the message by giving the full index to the Data client.
     * The index built by the Data server is [List of UserLight, List of VideoLight].
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        List<UserLight> users = (List<UserLight>) index.getUserList();
        List<VideoLight> videos = (List<VideoLight>) index.getVideoList();
        controller.interfaceCommDataClient.saveFullIndex(users, videos);
    }
}
