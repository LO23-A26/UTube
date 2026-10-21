package server.communication;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
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
    private final Thread readLoopThread;

    public ActiveClient(Socket socket) {
        this.socket = Objects.requireNonNull(socket, "socket cannot be null");
        this.uuid = UUID.randomUUID().toString();
        this.ipAddress = this.socket.getInetAddress() != null
                ? this.socket.getInetAddress().getHostAddress()
                : "unknown";
        this.port = this.socket.getPort();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
            this.in = reader;
            this.out = writer;
        } catch (IOException e) {
            closeQuietly(this.socket);
            throw new UncheckedIOException("Unable to create I/O streams for client socket", e);
        }

        this.connected = new AtomicBoolean(true);
        this.readLoopThread = Thread.ofVirtual()
            .name("active-client-read-" + this.uuid.substring(0, 8))
            .start(this::readLoop);
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

        closeQuietly(in);
        closeQuietly(out);
        closeQuietly(socket);
        readLoopThread.interrupt();
    }

    private boolean isConnected() {
        return connected.get() && !socket.isClosed();
    }

    private void readLoop() {
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