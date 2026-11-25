package common.dataclasses.communication.messages;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.communication.ClientMessage;


public class DownloadVideoRequestMessage implements ClientMessage {
    private static final System.Logger logger = System.getLogger(DownloadVideoRequestMessage.class.getName());

    private final UUID video_id;
    private final UUID requester_id;


    public DownloadVideoRequestMessage(UUID video_id, UUID requester_id) {
        this.video_id = video_id;
        this.requester_id = requester_id;
    }

    /**
     * Handles the request on the owner's side
     */
    @Override
    public void handle(CommClientController controller) {
        if (video_id == null || requester_id == null) {
            logger.log(ERROR, "Download request received without a video or a requester");
            return;
        }
        logger.log(INFO, "Download requested for video {0} by {1}", video_id, requester_id);

        byte[] video_bytes;
        try {
            video_bytes = controller.interfaceCommDataClient.askVideo(video_id, requester_id);
        } catch (RuntimeException ex) {
            logger.log(ERROR, "Could not get the requested video from Data", ex);
            return;
        }

        controller.clientConnectionsManager.openConnectionToClient(requester_id);
        controller.clientConnectionsManager.sendToClient(requester_id, new DownloadVideoResponseMessage(video_bytes));
    }
}