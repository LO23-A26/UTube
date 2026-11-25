package common.dataclasses.communication.messages;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.communication.ClientMessage;

/**
 * Server-to-client message telling a client that a video was deleted.
 * On reception, the video is removed from the local index through the data client.
 */
public class NotifyVideoDeletedMessage implements ClientMessage {
    private final UUID videoId;
    /**
     * @param videoId the UUID of the deleted video.
     */
    public NotifyVideoDeletedMessage(UUID videoId) {
        this.videoId = videoId;
    }
    /**
     * Removes the video from the local index.
     *
     * @param controller the client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.removeVideoFromUser(videoId);
    }
}