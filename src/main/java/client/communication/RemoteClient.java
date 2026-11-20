package client.communication;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
public class RemoteClient implements Serializable { // Other clients connected to the server
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
        this.clientController = clientController;
        try {
            // Each side creates ObjectOutputStream first and flushes it before
            // creating ObjectInputStream. This is required to avoid a stream
            // header deadlock between the two peers.
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.flush();
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            isConnected = true;
            startReceiving();
        } catch (IOException e) {
            isConnected = false;
            objectInputStream = null;
            objectOutputStream = null;
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            System.err.println("[RemoteClient] Could not connect to "
                    + ip.getHostAddress() + ":" + port + ": " + e.getMessage());
        }
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
            System.err.println("[RemoteClient] Cannot send message to "
                    + ip.getHostAddress() + ":" + port + ": not connected");
            return;
        }
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            System.err.println("[RemoteClient] Could not send message to "
                    + ip.getHostAddress() + ":" + port + ": " + e.getMessage());
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
        } catch (EOFException e) {
            // fermeture normale
            System.out.println("[RemoteClient] Connection closed by "
                    + ip.getHostAddress() + ":" + port);
        } catch (ClassNotFoundException | ClassCastException e) {
            System.err.println("[RemoteClient] Invalid message from "
                    + ip.getHostAddress() + ":" + port + ": " + e);
        } catch (IOException e) {
            if (isConnected()) {
                System.err.println("[RemoteClient] Socket error from "
                        + ip.getHostAddress() + ":" + port + ": " + e);
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
