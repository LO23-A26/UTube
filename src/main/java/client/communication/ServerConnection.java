package client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import common.dataclasses.communication.ServerMessage;

public class ServerConnection extends Thread {
    @SuppressWarnings("unused")
    private final ServerConfig serverConfig;
    private Socket serverSocket; // connection socket between client and server
    private ObjectOutputStream outToServer;
    @SuppressWarnings("unused")
    private final CommClientController commClientController; // necessary to get the local user's UUID.
    @SuppressWarnings("unused")
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
            System.getLogger(ServerConnection.class.getName()).log(System.Logger.Level.ERROR, "Connexion with server failed", ex);
        }
    }

    public void sendMessage(ServerMessage message) {
        try {
            this.outToServer.writeObject(message);
        } catch (IOException ex) {
            System.getLogger(ServerConnection.class.getName()).log(System.Logger.Level.ERROR, "Message could not be sent", ex);
        }
    }


    // independent thread execution
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
