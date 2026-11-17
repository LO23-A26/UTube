package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/**
 * Server-to-client message telling a client that another user shared a video.
 * On reception, the video is added to the local index through the data client.
 */
public class NotifyVideoSharedMessage implements ClientMessage {
    private final VideoLight video;
    /**
     * @param video the shared video.
     */
    public NotifyVideoSharedMessage(VideoLight video) {
        this.video = video;
    }

    /**
     * Adds the video to the local index.
     *
     * @param controller the client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.addRemoteVideo(video);
    }
}
