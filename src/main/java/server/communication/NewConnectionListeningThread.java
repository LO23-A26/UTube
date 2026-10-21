package server.communication;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

public class NewConnectionListeningThread extends Thread {

    private final ConnectionsManager connectionsManager;


    private static final int PORT = 9000;
    private static final int BACKLOG = 1024;
    private static final int MAX_CONNECTIONS = 10_000;
    private static final int READ_TIMEOUT_MS = (int) Duration.ofSeconds(30).toMillis();

    public NewConnectionListeningThread(ConnectionsManager connectionsManager) {
        this.connectionsManager = connectionsManager;
    }

    @Override
    public void run() {
        Semaphore connectionLimit = new Semaphore(MAX_CONNECTIONS);

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(PORT), BACKLOG);

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                System.out.println("Listening on " + serverSocket.getLocalSocketAddress());

                while (true) {
                    Socket socket = serverSocket.accept();

                    if (!connectionLimit.tryAcquire()) {
                        reject(socket);
                        continue;
                    }

                    try {
                        configure(socket);
                        executor.execute(() -> {
                            try {
                                handle(socket);
                            } finally {
                                connectionLimit.release();
                            }
                        });
                    } catch (IOException | RejectedExecutionException startupFailure) {
                        connectionLimit.release();
                        closeQuietly(socket);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to start listening thread: " + e.getMessage());
        }
    }

    private static void configure(Socket socket) throws SocketException {
        socket.setSoTimeout(READ_TIMEOUT_MS); // read timeout
        socket.setTcpNoDelay(true);           // reduce latency for small messages
        socket.setKeepAlive(true);            // OS-level dead peer detection; not a heartbeat substitute
    }

    // Un nouveau client vient de se connecter, on prend le socket
    // et crée un nouveau client avec
    private void handle(Socket socket) {
        // crée le nouveau client
        var client = new ActiveClient(socket); // faudra y ajouter les autres infos

        connectionsManager.connectNewClient(client);
    }

    private static void reject(Socket socket) {
        try (socket;
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {
            out.write("ERROR: server busy");
            out.newLine();
            out.flush();
        } catch (IOException ignored) {
            // The peer may already be gone.
        }
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}

// envoyer l'index via socket au client
