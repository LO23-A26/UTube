package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/**
 * Message sent to all clients when a new user connects to the server.
 * It contains the new user's information and their videos.
 */
public class NewUserNotificationMessage implements ClientMessage {
    private final UserLight new_user;
    private final VideoLight[] new_user_videos;

    public NewUserNotificationMessage(UserLight new_user, VideoLight[] new_user_videos) {
        this.new_user = new_user;
        this.new_user_videos = new_user_videos;
    }

    /**
     * Handles the message by saving the new user and their videos.
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.saveNewUser(this.new_user, this.new_user_videos[0]); // TODO ABSOLUTLY SEND ALL VIDEOS WHEN INTERFACE IS FIXED
    }
}