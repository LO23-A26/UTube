package client.communication;

import java.util.UUID;
import java.io.IOException;

import common.dataclasses.UserLight;
import common.dataclasses.communication.messages.VideoDetailsRequestMessage;
import common.dataclasses.communication.messages.DownloadVideoRequestMessage;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.comm.*;
import common.interfaces.data.InterfaceCommClientCallsDataClient;

import client.communication.implementations.*;


public class CommClientController {
    public final ClientConnectionsManager clientConnectionsManager; // the other clients, available for a direct connection
    private ClientListeningService clientListeningService;
    public ServerConnection serverConnection;

    /**
     * External interfaces, called by this controller.
     */
    public InterfaceCommunicationCallsIHMShare interfaceCommIHMShare;
    /**
     * External interface for data client calls. Kept public for now until the access level is clarified.
     */
    public InterfaceCommClientCallsDataClient interfaceCommDataClient;

    /**
     * Exposed interfaces for communication calls.
     */
    private final IHMMainCallsComm ihmMainCallsComm;
    private final IHMShareCallsComm ihmShareCallsComm;
    private final DataClientCallsComm dataClientCallsComm;

    /**
     * Sets the external interface used for communication with the IHM share module.
     * This must be called before {@code run()} to ensure proper initialization.
     *
     * @param interfaceCommIHMShare the interface implementation to use
     */
    public void setInterfaceCommIHMShare(InterfaceCommunicationCallsIHMShare interfaceCommIHMShare) {
        this.interfaceCommIHMShare = interfaceCommIHMShare;
    }

    /**
     * Sets the external interface used for communication with the data client module.
     * This must be called before {@code run()} to ensure proper initialization.
     *
     * @param interfaceCommDataClient the interface implementation to use
     */
    public void setInterfaceCommDataClient(InterfaceCommClientCallsDataClient interfaceCommDataClient) {
        this.interfaceCommDataClient = interfaceCommDataClient;
    }

    /**
     * Gets the interface that exposes communication calls to the data client module.
     *
     * @return the data client communication interface
     */
    public InterfaceDataCallsComm getDataCallsComm() {
        return dataClientCallsComm;
    }

    /**
     * Gets the interface that exposes communication calls to the IHM share module.
     *
     * @return the IHM share communication interface
     */
    public IHMShareCallsComm getIhmShareCallsComm() {
        return ihmShareCallsComm;
    }

    /**
     * Gets the interface that exposes communication calls to the IHM main module.
     *
     * @return the IHM main communication interface
     */
    public InterfaceIHMMainCallsComm getIhmMainCallsComm() {
        return ihmMainCallsComm;
    }

    /**
     * Creates the server connection object.
     * <ul>
     *   <li>Creates the socket.</li>
     *   <li>Sends the UUID to the server as an initial message.</li>
     * </ul>
     *
     * @param config the server configuration to use
     */
    public void initializeServerConnexion(ServerConfig config) {
        this.serverConnection = new ServerConnection(config, this);
        serverConnection.start();
    }

    /**
     * Closes the connection with the server by sending a disconnection request message.
     */
    public void closeServerConnection() {
        if (serverConnection == null) {
            return;
        }
        serverConnection.disconnect();
        serverConnection = null;
    }

    /**
     * Starts listening for direct connections from the other clients
     * @return the port on which this client listens for the other clients
     */
    public int startClientListeningService() throws IOException {
        if (clientListeningService == null) {
            clientListeningService = new ClientListeningService(this);
            clientListeningService.startListening();
        }
        return clientListeningService.getPort();
    }

    /**
     * Requests a video download from another client :
     *  - opens the connection with the client
     *  - sends the request to the client
     * @param videoId
     * @param clientId
     */
    public void requestVideoDownloadFromClient(UUID videoId, UUID clientId) {
        clientConnectionsManager.openConnectionToClient(clientId);
        UUID requester_id = interfaceCommDataClient.askRequesterId();
        clientConnectionsManager.sendToClient(clientId, new DownloadVideoRequestMessage(videoId, requester_id));
    }

    /**
     * Requests the details of a video from the client of its owner
     * @param requester the user who wants to see the details
     * @param owner the owner of the video
     * @param videoId the video whose details are requested
     */
    public void requestVideoDetailsFromClient(UserLight requester, UserLight owner, UUID videoId) {
        if (owner == null || owner.getUuid() == null) {
            return;
        }
        UUID ownerId = owner.getUuid();
        clientConnectionsManager.openConnectionToClient(ownerId);
        clientConnectionsManager.sendToClient(ownerId, new VideoDetailsRequestMessage(videoId, requester));
    }

    /**
     * Constructs a new communication client controller.
     * Requires no interface, but they must be set later.
     */
    public CommClientController() {
        /**
         * Sets external interfaces to {@code null}; they must be set later.
         */
        this.interfaceCommIHMShare = null;
        this.interfaceCommDataClient = null;

        /**
         * Properly initializes the exposed interfaces, passing this controller.
         */
        this.ihmMainCallsComm = new IHMMainCallsComm(this);
        this.ihmShareCallsComm = new IHMShareCallsComm(this);
        this.dataClientCallsComm = new DataClientCallsComm(this);

        this.clientConnectionsManager = new ClientConnectionsManager(this);
    }
}

