package common.dataclasses.communication.messages;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.UserLight;
import common.dataclasses.VideoFull;
import common.dataclasses.communication.ClientMessage;
import common.exceptions.AccessDeniedException;


public class VideoDetailsRequestMessage implements ClientMessage {

    private final UUID videoId;
    private final UserLight requester;

    /**
     * @param videoId   the video whose details are requested
     * @param requester the user who wants to see the details
     */
    public VideoDetailsRequestMessage(UUID videoId, UserLight requester) {
        this.videoId = videoId;
        this.requester = requester;
    }

    /**
     * Handles the request on the owner's side : asks Data for the details and gives them to the requester
     *
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        if (requester == null || requester.getUuid() == null) {
            throw new IllegalArgumentException("Requester cannot be null and must have a valid UUID");
        }

        VideoFull details;
        try {
            details = controller.interfaceCommDataClient.giveDetailsAccess(requester, videoId);
        } catch (AccessDeniedException e) {
            // The requester does not have access to the video details
            // TODO : Handle this case appropriately, e.g., send an error message back to the requester
            throw new IllegalStateException("Access denied for video with ID: " + videoId, e);
        }
        if (details == null) {
            throw new IllegalStateException("No details found for video with ID: " + videoId);
        }

        UUID requesterId = requester.getUuid();
        controller.clientConnectionsManager.openConnectionToClient(requesterId);
        controller.clientConnectionsManager.sendToClient(requesterId, new VideoDetailsResponseMessage(details));
    }
}