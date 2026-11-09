package client.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private ServerConnection serverConnection;
    private HashMap<UUID,ActiveClient> connectedClients;

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

    public void run() {
        // Ensure interfaces are set before running
        if (this.interfaceCommIHMShare == null || this.interfaceCommDataClient == null) {
            throw new IllegalStateException("CommClientController is not initialized. Please set the required interfaces before running.");
        }
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 9000), 2_000);
            socket.setSoTimeout(5_000);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // Envoi de l'UUID dès l'établissement de la connexion
            UUID clientUuid = UUID.randomUUID(); //!!! Mock UUID for testing purposes

            out.write(clientUuid.toString());
            out.newLine();
            out.flush();

            String response = in.readLine();
            System.out.println(response);
        } catch (IOException e) {
            System.err.println("Error during communication with server: " + e.getMessage());
        }
    }
}
