package server.communication;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import client.communication.RemoteClient;
import common.dataclasses.communication.messages.NotifyVideoDeletedMessage;
import common.dataclasses.communication.messages.NotifyVideoSharedMessage;
import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.messages.NewUserNotificationMessage;
import common.dataclasses.communication.messages.NotifyDisconnectionMessage;
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
     * @param newClient The new user.
     * @param videos The videos of the new user.
     */
    public void broadcastNewUserNotification(ActiveClient newClient, UserLight user,
                                             String ip, int port, Set<VideoLight> videos) {
        connectionsManager.broadcastMessageExcept(
                newClient.getUuid(),
                new NewUserNotificationMessage(user, ip, port, videos));
    }

    /**
     * Sends the full index to a new user (SendFullIndexMessage), with the address of the clients already connected for the P2P
     * @param receiver The new user.
     * @param index The full index.
     */
    public void sendFullIndex(ActiveClient receiver, Index index) {
        List<RemoteClient> others = connectionsManager.getRemoteClientsExcept(receiver.getUuid());
        connectionsManager.sendMessageToClient(receiver.getUuid(), new SendFullIndexMessage(index, others));
    }
    /**
     * Notifies every connected client except the sender that a video was shared.
     *
     * @param sender the client that shared the video, excluded from the broadcast.
     * @param video  the shared video.
     */
    public void broadcastVideoShared(ActiveClient sender, VideoLight video) {
        connectionsManager.broadcastMessageExcept(sender.getUuid(), new NotifyVideoSharedMessage(video));
    }
    /**
     * Notifies every connected client except the sender that a video was deleted.
     *
     * @param sender  the client that deleted the video, excluded from the broadcast.
     * @param videoId the UUID of the deleted video.
     */
    public void broadcastVideoDeleted(ActiveClient sender, UUID videoId) {
        connectionsManager.broadcastMessageExcept(sender.getUuid(), new NotifyVideoDeletedMessage(videoId));
    }

    public void handleClientDisconnected(ActiveClient client) {
        // Only the first call for this client goes further (voluntary + socket closed = 1 treatment)
        if (!connectionsManager.disconnectClient(client)) {
            return;
        }
        UUID userId = client.getUuid();
        if (userId == null) {
            return; // never registered: nothing in the index, nobody to notify
        }
        interfaceCommServerCallsDataServer.removeUser(userId);
        connectionsManager.broadcastMessage(new NotifyDisconnectionMessage(userId));
    }

}