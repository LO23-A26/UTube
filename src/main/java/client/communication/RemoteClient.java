package client.communication;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

import common.dataclasses.communication.ClientMessage;

/**
 * Represents a remote client connected to the server.
 * It can be connected to the local client for peer-to-peer communication.
 * It handles sending and receiving messages to/from the remote client.
 * When not connected, it can be sent to share the information about a remote client.
 * When connected, it holds itself the socket to the client.
 */
public class RemoteClient implements Serializable { 

    private static final System.Logger logger = System.getLogger(RemoteClient.class.getName());
    
    // Other clients connected to the server
    private final UUID uuid;
    private final InetAddress ip;
    private final int port;

    // Whether the local client is connected to this remote client
    // transient to make sure it comes out as false when sent
    private transient boolean isConnected;

    // Object streams, transient -> not serialized
    private transient ObjectInputStream objectInputStream;
    private transient ObjectOutputStream objectOutputStream;
    private transient Socket socket;
    private transient Thread receiveThread;
    private transient CommClientController clientController;

    /**
     * @param ip   the IP address of the client
     * @param port the port on which the client listens for the other clients
     * @param uuid the UUID of the user of the client
     */
    public RemoteClient(InetAddress ip, int port, UUID uuid) {
        this.ip = ip;
        this.port = port;
        this.uuid = uuid;
    }

    /** @return the UUID of the user of the client */
    public UUID getUuid() {
        return uuid;
    }

    /** @return the IP address of the client */
    public InetAddress getIP() {
        return ip;
    }

    /** @return the port on which the client listens for the other clients */
    public int getPort() {
        return port;
    }

    /** @return whether the local client is connected to this remote client */
    public boolean isConnected() {
        return isConnected && socket != null && socket.isConnected() && !socket.isClosed();
    }

    /**
     * Establishes a connection to the remote client
     *  - Creates a socket to the remote client
     *  - Starts the listening thread
     *  - Sets connection status to true
     * This is required to send messages to the remote client.
     * Ths controller is used to handle the received messages.
     * @param socket
     * @param clientController
     */
    public synchronized void initiateConnection(Socket socket) {
        if (socket == null) {
            return;
        }
        if (isConnected()) {
            return;
        }

        this.socket = socket;
        try {
            // Each side creates ObjectOutputStream first and flushes it before
            // creating ObjectInputStream. This is required to avoid a stream
            // header deadlock between the two peers.
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            objectOutputStream.writeObject(clientController.interfaceCommDataClient.askRequesterId()); // tells the other client who we are
            objectOutputStream.flush();
            startReceiving();
        } catch (IOException ex) {
            isConnected = false;
            objectInputStream = null;
            objectOutputStream = null;
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            logger.log(ERROR, "Could not connect to "
                    + ip.getHostAddress() + ":" + port, ex);
        }
    }

    /**
     * 
     * @param socket
     * @param reader
     * @param writer
     */
    public synchronized void registerConnection(Socket socket, ObjectInputStream reader, ObjectOutputStream writer) {
        if (isConnected()) {
            logger.log(ERROR, "client is already connected via a socket. Can't add another.");
            return;
        }

        this.socket = socket;
        this.objectInputStream = reader;
        this.objectOutputStream = writer;
        isConnected = true;
        startReceiving();
    }

    /**
     * Disconnects from the remote client, closing the socket and streams.
     */
    public synchronized void disconnect() {
        isConnected = false;
        objectInputStream = null;
        objectOutputStream = null;
        clientController = null;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            socket = null;
        }
    }

    /**
     * Sends a message to the remote client.
     * WARNING: Prefer calling sendToClient() in ClientConnectionsManager instead
     * of calling this method directly
     * @see ClientConnectionsManager#sendToClient(RemoteClient, ClientMessage)
     * @param message the message to send
     */
    public synchronized void sendMessage(ClientMessage message) {
        if (!isConnected()) {
            logger.log(INFO, "Cannot send message to {0}:{1}: not connected", new Object[]{ip.getHostAddress(), port});
            return;
        }
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException ex) {
            logger.log(ERROR, "Could not send message to "
                    + ip.getHostAddress() + ":" + port, ex);
            disconnect();
        }
    }

    /**
     * Setter for the client controller. 
     * Must be called inside of ClientConnectionsManager.addClient()
     * @see ClientConnectionsManager#addClient(RemoteClient)
     * @param clientController
     */
    public void setClientController(CommClientController clientController) {
        this.clientController = clientController;
    }

    

    /**
     * Starts the receiving thread that listens for messages from the remote client.
     * The thread runs receiveLoop()
     */
    private void startReceiving() {
        if (receiveThread != null && receiveThread.isAlive()) {
            return;
        }

        receiveThread = new Thread(this::receiveLoop, "remote-client-receiver-" + uuid);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    /**
     * The main loop of the receiving thread, which listens for messages from the remote client.
     */
    private void receiveLoop() {
        try {
            while (isConnected()) {
                handleMessage((ClientMessage) objectInputStream.readObject());
            }
        } catch (EOFException ex) {
            // fermeture normale
            logger.log(INFO, "Connection closed by {0}:{1}", new Object[]{ip.getHostAddress(), port});
        } catch (ClassNotFoundException | ClassCastException ex) {
            logger.log(ERROR, String.format("Invalid message from %s:%d", ip.getHostAddress(), port), ex);
        } catch (IOException ex) {
            if (isConnected()) {
                logger.log(ERROR, String.format("Socket error from %s:%d", ip.getHostAddress(), port), ex);
            }
        } finally {
            disconnect();
        }
    }

    /**
     * Handles a received message from the remote client.
     * It uses the handle() method in the client messages
     * @param message the received message
     */
    private void handleMessage(ClientMessage message) {
            if (clientController == null) {
                throw new IllegalStateException("RemoteClient received a ClientMessage but no CommClientController was provided");
            }
            message.handle(clientController);
    }
}
