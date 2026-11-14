package common.dataclasses.communication.messages;

import java.util.UUID;

import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

/** Sent by a client that deletes one of its shared videos. */
public class DeleteVideoMessage implements ServerMessage {
    private final UUID videoId;

    public DeleteVideoMessage(UUID videoId) {
        this.videoId = videoId;
    }

    @Override
    public void handle(CommServerController controller, ActiveClient sender) {
        controller.interfaceCommServerCallsDataServer.deleteIndex(videoId);
        controller.broadcastVideoDeleted(sender, videoId);
    }
}