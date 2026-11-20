package client.communication;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.Logger.Level.ERROR;
import java.net.Socket;
import java.net.SocketTimeoutException;

import common.dataclasses.communication.ClientMessage;
import common.dataclasses.communication.ServerMessage;
import common.dataclasses.communication.messages.DisconnectionRequestMessage;

public class ServerConnection extends Thread {

    private static final System.Logger logger = System.getLogger(ServerConnection.class.getName());

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
            logger.log(ERROR, "Connexion with server failed", ex);
        }
    }

    public void sendMessage(ServerMessage message) {
        try {
            this.outToServer.writeObject(message);
        } catch (IOException ex) {
            logger.log(ERROR, "Message could not be sent", ex);
        }
    }


    // independent thread execution
    @Override
    public void run() {
        try {
            while (!serverSocket.isClosed()) {
                try {
                    ClientMessage message = (ClientMessage) inFromServer.readObject();
                    message.handle(commClientController);
                } catch (SocketTimeoutException ignored) {
                    // pas de timeout configuré côté client, par sécurité
                }
            }
        } catch (EOFException ex) {
            logger.log(ERROR, "Server closed the connection", ex);
        } catch (IOException | ClassNotFoundException ex) {
            if (!serverSocket.isClosed()) {
                logger.log(ERROR, "IO Error in server : ", ex);
            }
        } finally {
            try { serverSocket.close(); } catch (IOException ignored) { }
        }
    }

    public void disconnect() {
        sendMessage(new DisconnectionRequestMessage());
        try { serverSocket.close(); } catch (IOException ignored) { }
    }
}
