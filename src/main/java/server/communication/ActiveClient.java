package server.communication;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import common.dataclasses.communication.ClientMessage;
import common.dataclasses.communication.ServerMessage;

/**
 * An active client connected to the server.
 */
public class ActiveClient {
    private final UUID uuid;
    private final String ipAddress;
    private final int port;

    private final Socket socket;
    private final ObjectInputStream in; // Ici mettre des ObjectStreams ? 
    private final ObjectOutputStream out;
    private final AtomicBoolean connected;
    private final CommServerController serverController;

    public ActiveClient(
            Socket socket, 
            UUID uuid, 
            ObjectInputStream reader, 
            ObjectOutputStream writer, 
            CommServerController serverController
        ) {
        this.socket = Objects.requireNonNull(socket, "socket cannot be null");
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");

        this.ipAddress = this.socket.getInetAddress() != null
                ? this.socket.getInetAddress().getHostAddress()
                : "unknown";

        this.port = this.socket.getPort();

        this.in = Objects.requireNonNull(reader);
        this.out = Objects.requireNonNull(writer);
        
        this.connected = new AtomicBoolean(true);
        this.serverController = serverController;
    }

    /**
     * Returns the UUID of the client.
     * @return the UUID of the client.
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the IP address of the client.
     * @return the IP address of the client.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Returns the port of the client.
     * @return the port of the client.
     */
    public int getPort() {
        return port;
    }

    /**
     * Sends a message to the client.
     * @param message the message to send.
     * @throws IOException 
     */
    public void send(ClientMessage message) throws IOException {
        if (!isConnected()) {
            throw new IOException("Client " + uuid + " is disconnected");
        }

        synchronized (out) {
            out.writeObject(message);
        }
    }

    /**
     * Closes the client connection and releases resources.
     */
    public void close() {
        if (!connected.compareAndSet(true, false)) {
            return;
        }

        closeQuietly(socket); // unblocks the read loop
        closeQuietly(in);
        closeQuietly(out);
    }

    private boolean isConnected() {
        return connected.get() && !socket.isClosed();
    }

    /**
     * Read loop of this client: blocks until the client leaves (or an error occurs).
     */
    public void readLoop() {
        try {
            while (connected.get()) {
                ServerMessage message;
                try {
                        message = (ServerMessage) in.readObject();
                        handleMessage(message); // ça il faut que ça handle les objets
                } catch (SocketTimeoutException timeout) {
                    // Socket timeout is expected when no client data arrives; keep waiting.
                } catch (ClassNotFoundException ex) {
                    System.getLogger(ActiveClient.class.getName()).log(System.Logger.Level.ERROR, (String) "Recieved object is not of class ServerMessage", ex);
                } 
            }
        } catch (IOException e) {
            if (connected.get()) {
                System.err.println("Read error from client " + uuid + ": " + e.getMessage());
            }
        } finally {
            close();
        }
    }

    private void handleMessage(ServerMessage message) {
        // Il faudra call le handler dans le message une fois qu'on a une classe message
        // v1 avec du texte pr l'instant : on affiche la ligne recue et on repond "OK ..." pour verifier que la connexion marche.
        System.out.println("received from " + uuid + " : " + message);
        message.handle(this.serverController);
        
    }
    
    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    private static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}