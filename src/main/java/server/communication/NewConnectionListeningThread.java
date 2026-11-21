package server.communication;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
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

    public int port = 9000;
    private static final System.Logger logger = System.getLogger(NewConnectionListeningThread.class.getName());

    private final ConnectionsManager connectionsManager;
    private final CommServerController serverController;
    private Semaphore connectionLimit;

    private static final int BACKLOG = 1024;
    private static final int MAX_CONNECTIONS = 10_000;
    private static final int READ_TIMEOUT_MS = (int) Duration.ofSeconds(30).toMillis();

    /**
     * Constructor
     */
    public NewConnectionListeningThread(ConnectionsManager connectionsManager, CommServerController serverController) {
        this.connectionsManager = connectionsManager;
        this.serverController = serverController;
        this.connectionLimit = new Semaphore(MAX_CONNECTIONS);
    }

    @Override
    public void run() {

        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port), BACKLOG);

            try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
                logger.log(INFO, "Listening on {0}", serverSocket.getLocalSocketAddress());

                while (true) {
                    Socket socket = serverSocket.accept();
                    createAndStartConnectionThread(socket, executor);
                }
            }
        } catch (IOException ex) {
            logger.log(ERROR, "Unable to start listening thread", ex);
        }
    }

    private static void configure(Socket socket) throws SocketException {
        socket.setSoTimeout(READ_TIMEOUT_MS); // read timeout
        socket.setTcpNoDelay(true);           // reduce latency for small messages
        socket.setKeepAlive(true);            // OS-level dead peer detection; not a heartbeat substitute
    }

    private void createAndStartConnectionThread(Socket socket, ExecutorService executor) {
        if (!connectionLimit.tryAcquire()) {
            reject(socket);
            return;
        }

        try {
            configure(socket);
            executor.execute(() -> {
                try {
                    handleNewConnection(socket);
                } finally {
                    connectionLimit.release();
                }
            });
        } catch (IOException | RejectedExecutionException startupFailure) {
            logger.log(ERROR, "Unable to start or schedule a client connection", startupFailure);
            connectionLimit.release();
            closeQuietly(socket);
        }
    }

    /**
     * Recieves the first message (the UUID of the client) and starts the
     * handeling loop for this client
     */
    @SuppressWarnings("ConvertToTryWithResources")
    private void handleNewConnection(Socket socket) {
        ObjectOutputStream writer;
        ObjectInputStream reader;
        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
            writer.flush();
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            logger.log(ERROR, "Could not initialize communication streams for a new connection", ex);
            closeQuietly(socket);
            return;
        }

        // Lecture du premier message := l'UUID du client
        UUID uuid;
        try {
            uuid = (UUID) reader.readObject(); // Est ce qu'on reçoit l'UUID comme ça ou dans un autre message ?
            if (uuid == null) {
                logger.log(ERROR, "UUID sent is null. Closing connection");
                socket.close();
                return;
            }
        } catch (ClassNotFoundException ex) {
            logger.log(System.Logger.Level.ERROR, "Object sent at initialization was not a UUID", ex);
            closeQuietly(socket);
            return;
        } catch (IOException ex) {
            logger.log(ERROR, "Could not read the UUID sent during connection initialization", ex);
            closeQuietly(socket);
            return;
        }

        // Création du ActiveClient avec son UUID
        // et les flux déjà initialisés
        var client = new ActiveClient(socket, uuid, reader, writer, serverController);
        connectionsManager.connectNewClient(client);

        try { // Ici on lance la boucle de handle
            client.readLoop(); // blocks until the client leaves 
        } finally {
            serverController.handleClientDisconnected(client);
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
