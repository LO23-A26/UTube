package client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.dataclasses.communication.ServerMessage;

public class ServerConnection extends Thread {
    private ServerConfig serverConfig;
    private Socket serverSocket; // connection socket between client and server
    private ObjectOutputStream outToServer;
    private CommClientController commClientController; // necessary to get the local user's UUID.
    private ObjectInputStream inFromServer;

    // getters and setters
    
    /**
     * Constructor
     * Creates the socket with the server and sends UUID to server
     */
    public ServerConnection(ServerConfig config, CommClientController controller) {
        this.serverConfig = config;
        this.commClientController = controller;

        try {
            this.serverSocket = new Socket(config.getIP(), config.getPort());
            this.outToServer = new ObjectOutputStream(this.serverSocket.getOutputStream());
            this.outToServer.flush();
            this.inFromServer = new ObjectInputStream(this.serverSocket.getInputStream());
            this.outToServer.writeObject(controller.interfaceCommDataClient.askRequesterId());
        } catch (IOException ex) {
            System.getLogger(ServerConnection.class.getName()).log(System.Logger.Level.ERROR, (String) "Connexion with server failed", ex);
        }
    }

    public void send_message(ServerMessage message) {
        try {
            this.outToServer.writeObject(message);
        } catch (IOException ex) {
            System.getLogger(ServerConnection.class.getName()).log(System.Logger.Level.ERROR, (String) "Message could not be sent", ex);
        }
    }


    // independent thread execution
    @Override
    public void run() {
    }

}
