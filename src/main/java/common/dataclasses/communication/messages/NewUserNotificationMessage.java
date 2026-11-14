package common.dataclasses.communication.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

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
    private final Set<VideoLight> new_user_videos; // Not used yet, waiting for saveNewUser to be correct
    private final String new_user_ip;
    private final int new_user_port;

    public NewUserNotificationMessage(UserLight new_user, String new_user_ip, int new_user_port, Set<VideoLight> new_user_videos) {
        this.new_user = new_user;
        this.new_user_ip = new_user_ip;
        this.new_user_port = new_user_port;
        this.new_user_videos = new_user_videos;
    }

    /**
     * Handles the message by saving the new user and their videos.
     * Adds the client information in the map of active users
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        
        InetAddress ip; // Is converted here, but should be sent to server as InetAdress directly 
        try {
            ip = InetAddress.getByName(new_user_ip);
        } catch (UnknownHostException ex) {
            System.getLogger(NewUserNotificationMessage.class.getName()).log(System.Logger.Level.ERROR, "New client ip adress is invalid, creation failed", ex);
            return;
        }
        
        controller.addClient(ip, new_user_port, new_user.getUuid());
        controller.interfaceCommDataClient.saveNewUser(this.new_user, null); // TODO ABSOLUTLY SEND ALL VIDEOS WHEN INTERFACE IS FIXED

    }
}