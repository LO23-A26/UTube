package server.communication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;

public class NewConnectionListeningThread extends Thread {

    private final ConnectionsManager connectionsManager;
    private final CommServerController serverController;


    private static final int PORT = 9000;
    private static final int BACKLOG = 1024;
    private static final int MAX_CONNECTIONS = 10_000;
    private static final int READ_TIMEOUT_MS = (int) Duration.ofSeconds(30).toMillis();

    /**
     * Constructor
     */
    public NewConnectionListeningThread(ConnectionsManager connectionsManager, CommServerController serverController) {
        this.connectionsManager = connectionsManager;
        this.serverController = serverController;
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
                                handle_new_connection(socket);
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

    private void handle_new_connection(Socket socket) {
    try {
        var reader = new ObjectInputStream(socket.getInputStream());
        var writer = new ObjectOutputStream(socket.getOutputStream());
        // Lecture du premier message := l'UUID du client
        
        UUID uuid;
        try {
            uuid = (UUID) reader.readObject(); // Est ce qu'on reçoit l'UUID comme ça ou dans un autre message ?
            if (uuid == null) {
                socket.close();
                return;
            }
        } catch (ClassNotFoundException ex) {
            System.getLogger(NewConnectionListeningThread.class.getName()).log(System.Logger.Level.ERROR, (String) "Object sent at initialization was not a UUID", ex);
            socket.close();
            return;
        }

        // Création du ActiveClient avec son UUID
        // et les flux déjà initialisés
        var client = new ActiveClient(socket, uuid, reader, writer, serverController);
        connectionsManager.connectNewClient(client);

        try { // Ici on lance la boucle de handle
            client.readLoop(); // blocks until the client leaves 
        } finally {
            connectionsManager.disconnectClient(client);
        }

    } catch (IOException e) {
        System.err.println("Unable to initialize client: " + e.getMessage());
        closeQuietly(socket);
    }
}

    private static void reject(Socket socket) { // Trouver mieux pour envoyer un objet message. 
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
