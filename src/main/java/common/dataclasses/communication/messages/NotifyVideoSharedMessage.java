package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/** Tells a client that another user shared a video. */
public class NotifyVideoSharedMessage implements ClientMessage {
    private final VideoLight video;

    public NotifyVideoSharedMessage(VideoLight video) {
        this.video = video;
    }

    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.addRemoteVideo(video);
    }
}
