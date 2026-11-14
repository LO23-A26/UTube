package client.communication;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.comm.*;
import common.interfaces.data.InterfaceCommClientCallsDataClient;

import client.communication.implementations.*;

/**
 * Controller for the communication client, entry point for the communication module.
 * How to use:
 * <ol>
 *   <li>Create an instance of {@code CommClientController}; the constructor requires no interface.</li>
 * </ol>
 */
public class CommClientController {
    private final HashMap<UUID,ActiveClient> connectedClients;
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
    }

    /**
     * Adds a connected client to the controller.
     *
     * @param ip_adress the client's IP address
     * @param port the client's port
     * @param uuid the client's UUID
     */
    public void addClient(InetAddress ip_adress, int port, UUID uuid) {
        connectedClients.put(uuid, new ActiveClient(ip_adress, port, uuid));
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

        this.connectedClients = new HashMap<>();
    }
}
