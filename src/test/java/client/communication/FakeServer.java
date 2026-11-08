package client.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 FakeServer accepts connections, reads the first line sent by each client (the UUID), stores it, and answers like the real server
 TODO: when the client switches to ObjectOutputStream (ticket #48), update this class to read Message objects instead of text lines
 */
public class FakeServer implements AutoCloseable {

    private final ServerSocket serverSocket;
    private final Thread acceptThread;
    private final BlockingQueue<String> receivedUuids = new LinkedBlockingQueue<>();


    //Opens the server socket and starts accepting clients in a background thread.
    public FakeServer(int port) throws IOException {
        this.serverSocket = new ServerSocket();
        this.serverSocket.setReuseAddress(true);
        this.serverSocket.bind(new InetSocketAddress(port));

        this.acceptThread = new Thread(this::acceptLoop, "fake-server-accept");
        this.acceptThread.setDaemon(true); // never blocks the JVM from exiting
        this.acceptThread.start();
    }

    //Waits for the next UUID sent by a client.
    public String waitForUuid(long timeoutMs) throws InterruptedException {
        return receivedUuids.poll(timeoutMs, TimeUnit.MILLISECONDS);
    }

    private void acceptLoop() {
        while (!serverSocket.isClosed()) {
            try (Socket client = serverSocket.accept()) {
                handleClient(client);
            } catch (IOException e) {
                // Thrown when close() is called while accept() is blocking: stop the loop
            }
        }
    }

    private void handleClient(Socket client) throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8));

        String uuid = in.readLine();
        if (uuid != null) {
            receivedUuids.add(uuid);
            out.write("UUID received: " + uuid);
            out.newLine();
            out.flush();
        }
    }

    //Stops the server and frees the port.
    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}