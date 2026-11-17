package common.dataclasses.communication.messages;

import common.dataclasses.VideoLight;
import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

/**
 * Client-to-server message sent when a user shares a video.
 * The server updates the data server, then notifies the other clients.
 */
public class ShareVideoMessage implements ServerMessage {
    private final VideoLight video;
    /**
     * @param video the shared video.
     */
    public ShareVideoMessage(VideoLight video) {
        this.video = video;
    }
    /**
     * Updates the index on the data server, then broadcasts the new video to every other client.
     *
     * @param controller the server controller.
     * @param sender     the client that sent the message, excluded from the broadcast.
     */
    @Override
    public void handle(CommServerController controller, ActiveClient sender) {
        controller.interfaceCommServerCallsDataServer.updateIndex(video);
        controller.broadcastVideoShared(sender, video);
    }
}