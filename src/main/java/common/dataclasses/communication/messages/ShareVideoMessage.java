package common.dataclasses.communication.messages;

import common.dataclasses.VideoLight;
import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

/** Sent by a client that shares a video. */
public class ShareVideoMessage implements ServerMessage {
    private final VideoLight video;

    public ShareVideoMessage(VideoLight video) {
        this.video = video;
    }

    @Override
    public void handle(CommServerController controller, ActiveClient sender) {
        controller.interfaceCommServerCallsDataServer.updateIndex(video);
        controller.broadcastVideoShared(sender, video);
    }
}