package server.communication;

import java.util.ArrayList;
import java.util.Set;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;
import common.dataclasses.communication.messages.NewUserNotificationMessage;
import common.dataclasses.communication.messages.SendFullIndexMessage;
import common.interfaces.comm.InterfaceDataServerCallsCommServer;
import common.interfaces.data.InterfaceCommServerCallsDataServer;
import server.communication.implementations.DataServerCallsCommServer;

/**
 * Controller for the communication server, entry point for the communication module on the server side.
 * How to use :
 * 1. Create an instance of CommServerController, the constructor requires no interface
 * 2. Set the external interface with setInterfaceCommServerCallsDataServer()
 * 3. Call start()
 */
public class CommServerController {
    private final ConnectionsManager connectionsManager;
    private final NewConnectionListeningThread newConnectionListeningThread;

    // External interface, called by this controller
    public InterfaceCommServerCallsDataServer interfaceCommServerCallsDataServer; // public: the messages call it through the controller

    // Exposed interface, called by the Data server
    private final DataServerCallsCommServer dataServerCallsCommServer;

    /**
     * Constructor for the CommServerController class.
     * Requires no interface, but the external one must be set before start().
     */
    public CommServerController() {
        // sets the external interface to null, it must be set later
        this.interfaceCommServerCallsDataServer = null;

        this.connectionsManager = new ConnectionsManager();
        this.newConnectionListeningThread = new NewConnectionListeningThread(connectionsManager, this);

        // Properly initialize the exposed interface, passing this controller
        this.dataServerCallsCommServer = new DataServerCallsCommServer(this);
    }

    // Setter for the external interface, must be called before start()
    public void setInterfaceCommServerCallsDataServer(InterfaceCommServerCallsDataServer interfaceCommServerCallsDataServer) {
        this.interfaceCommServerCallsDataServer = interfaceCommServerCallsDataServer;
    }

    // Getter for the exposed interface
    public InterfaceDataServerCallsCommServer getDataServerCallsCommServer() {
        return dataServerCallsCommServer;
    }

    /**
     * Starts the server: begins listening for new connections(the external interface must have been set first)
     */
    public void start() {
        if (this.interfaceCommServerCallsDataServer == null) {
            throw new IllegalStateException("CommServerController is not initialized. Please set the required interface before starting.");
        }
        newConnectionListeningThread.start();
    }

    /** 
     * Broadcasts a notification (NewUserNotificationMessage) to all connected clients about a new user.
     * @param new_user The new user.
     * @param new_user_videos The videos of the new user.
     */
    public void broadcastNewUserNotification(UserLight new_user, String new_user_ip, int new_user_port, Set<VideoLight> new_user_videos) {
        ClientMessage message = new NewUserNotificationMessage(
            new_user, 
            new_user_ip, 
            new_user_port, 
            new_user_videos
        );
        
        connectionsManager.broadcastMessage(message);
    }

    /**
     * Sends the full index to a new user (SendFullIndexMessage).
     * @param new_user The new user.
     * @param index The full index.
     */
    public void sendFullIndex(UserLight new_user, ArrayList<Object> index) {
        SendFullIndexMessage message = new SendFullIndexMessage(
            index
        );
        connectionsManager.sendMessageToClient(new_user.getUuid(), message);
    }
}