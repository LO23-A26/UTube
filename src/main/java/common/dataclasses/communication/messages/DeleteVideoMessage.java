package common.dataclasses.communication.messages;

import java.util.UUID;

import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

/**
 * Client-to-server message sent when a user deletes one of their shared videos.
 * The server updates the data server, then notifies the other clients.
 */
public class DeleteVideoMessage implements ServerMessage {
    private final UUID videoId;
    /**
     * @param videoId the UUID of the video to delete.
     */
    public DeleteVideoMessage(UUID videoId) {
        this.videoId = videoId;
    }
    /**
     * Removes the video from the index on the data server, then broadcasts the deletion to every other client.
     *
     * @param controller the server controller.
     * @param sender     the client that sent the message, excluded from the broadcast.
     */
    @Override
    public void handle(CommServerController controller, ActiveClient sender) {
        controller.interfaceCommServerCallsDataServer.deleteIndex(videoId);
        controller.broadcastVideoDeleted(sender, videoId);
    }
}