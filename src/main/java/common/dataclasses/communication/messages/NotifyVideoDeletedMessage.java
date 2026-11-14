package common.dataclasses.communication.messages;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.communication.ClientMessage;

/** Tells a client that a video was deleted. */
public class NotifyVideoDeletedMessage implements ClientMessage {
    private final UUID videoId;

    public NotifyVideoDeletedMessage(UUID videoId) {
        this.videoId = videoId;
    }

    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.removeVideoFromUser(videoId);
    }
}