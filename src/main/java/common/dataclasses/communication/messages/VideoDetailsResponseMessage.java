package common.dataclasses.communication.messages;


import client.communication.CommClientController;
import common.dataclasses.VideoFull;
import common.dataclasses.communication.ClientMessage;

/**
 * Message sent by the client of the owner of a video to the client that asked for its details
 */
public class VideoDetailsResponseMessage implements ClientMessage {
    private final VideoFull details;

    public VideoDetailsResponseMessage(VideoFull details) {
        this.details = details;
    }

    /**
     * Handles the answer on the requester's side: gives the details of the video to IHM Share.
     *
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        if (details != null) {
            controller.interfaceCommIHMShare.displayVideoDetails(details);
        }
    }
}



