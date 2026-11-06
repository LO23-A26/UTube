package server.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An active client connected to the server.
 */
public class ActiveClient {
    private final String uuid;
    private final String ipAddress;
    private final int port;

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final AtomicBoolean connected;

    public ActiveClient(
        Socket socket,
        String uuid,
        BufferedReader reader,
        BufferedWriter writer
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
    }

    /**
     * Returns the UUID of the client.
     * @return the UUID of the client.
     */
    public String getUuid() {
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
    public void send(String message) throws IOException {
        if (!isConnected()) {
            throw new IOException("Client " + uuid + " is disconnected");
        }

        synchronized (out) {
            out.write(message);
            out.newLine();
            out.flush();
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
                try {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    }
                    handleMessage(line);
                } catch (SocketTimeoutException timeout) {
                    // Socket timeout is expected when no client data arrives; keep waiting.
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

    private void handleMessage(String message) {
        // Il faudra call le handler dans le message une fois qu'on a une classe message
        // v1 avec du texte pr l'instant : on affiche la ligne recue et on repond "OK ..." pour verifier que la connexion marche.
        System.out.println("received from " + uuid + " : " + message);
        try {
            out.write("OK " + message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            close();
        }
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