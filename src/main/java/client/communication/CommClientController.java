package client.communication;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.UUID;

import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.comm.*;
import common.interfaces.data.InterfaceCommClientCallsDataClient;

import client.communication.implementations.*;

/* 
 * Controller for the communication client, entry point for the communication module.
 * How to use :
 * 1. Create an instance of CommClientController, the constructor requires no interface
 */
public class CommClientController {
    private ClientListeningService clientListener;
    private ServerListeningService  serverListener;
    private HashMap<UUID,ActiveClient> connectedClients;
    public ServerConnection serverConnection;

    // External interfaces, called by this controller
    private InterfaceCommunicationCallsIHMShare interfaceCommIHMShare;
    public InterfaceCommClientCallsDataClient interfaceCommDataClient; // I let it public for now, unsure what the proper access should be

    // Exposed interfaces for communication calls
    private IHMMainCallsComm ihmMainCallsComm;
    private IHMShareCallsComm ihmShareCallsComm;
    private DataClientCallsComm dataClientCallsComm;

    // Setters for external interfaces, must be called before run() to ensure proper initialization
    public void setInterfaceCommIHMShare(InterfaceCommunicationCallsIHMShare interfaceCommIHMShare) {
        this.interfaceCommIHMShare = interfaceCommIHMShare;
    }

    public void setInterfaceCommDataClient(InterfaceCommClientCallsDataClient interfaceCommDataClient) {
        this.interfaceCommDataClient = interfaceCommDataClient;
    }

    // Getters for exposed interfaces
    public InterfaceDataCallsComm getDataCallsComm() {
        return dataClientCallsComm;
    }

    public IHMShareCallsComm getIhmShareCallsComm() {
        return ihmShareCallsComm;
    }

    public InterfaceIHMMainCallsComm getIhmMainCallsComm() {
        return ihmMainCallsComm;
    }

    /**
     * Creates the server connexion object 
     * - Creates the socket
     * - Sends the UUID to the server as an initial message
     */
    public void initialize_server_connexion(ServerConfig config) {
        this.serverConnection = new ServerConnection(config, this);
        serverConnection.start();
    }

    public void addClient(InetAddress ip_adress, int port, UUID uuid) {
        connectedClients.put(uuid, new ActiveClient(ip_adress, port, uuid));
    }

    /*
     * Constructor for the CommClientController class.
     * Requires no interface, but they must be set later.
     */
    public CommClientController() {
        // sets external interfaces to null, they must be set later
        this.interfaceCommIHMShare = null;
        this.interfaceCommDataClient = null;

        // Properly initialize the exposed interfaces, passing this controller
        this.ihmMainCallsComm = new IHMMainCallsComm(this);
        this.ihmShareCallsComm = new IHMShareCallsComm(this);
        this.dataClientCallsComm = new DataClientCallsComm(this);

        this.connectedClients = new HashMap<>();
        this.clientListener = new ClientListeningService(this);
        this.serverListener = new ServerListeningService(this);
    }
}
