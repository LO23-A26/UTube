package client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that listens for peer-to-peer connections from the other clients
 * Gives the client its own listening port on which the other clients can connect directly
 */
public class ClientListeningService extends Thread {
    
    private static final System.Logger logger = System.getLogger(ClientListeningService.class.getName());

    private final CommClientController clientController; 
    private ServerSocket listeningSocket;

    // constructor
    public ClientListeningService(CommClientController clientController) {
        this.clientController = clientController;
        this.setName("client-listening-service");
        this.setDaemon(true);
    }

    /**
     * Opens the listening socket, then starts accepting the connections of the other clients and returns the port to which the client listens
     */
    public int startListening() throws IOException {
        listeningSocket = new ServerSocket(0);
        start();
        logger.log(INFO, "[ClientListeningService] Listening for the other clients on port {0}", getPort());
        return getPort();
    }

    // returns -1 if the service has not started
    public int getPort() {
        return listeningSocket == null ? -1 : listeningSocket.getLocalPort();
    }


    // thread execution main loop
    @Override
    public void run(){
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (!listeningSocket.isClosed()) {
                Socket peerSocket = listeningSocket.accept(); // blocks until another client connects
                executor.submit(() -> handleConnection(peerSocket));
            }
        } catch (SocketException ex) {
            logger.log(ERROR, "Stopped listening for the other clients", ex);
        } catch (IOException ex) {
            logger.log(ERROR, "Listening stopped unexpectedly: ", ex);
        }
    }

    @SuppressWarnings("ConvertToTryWithResources")
    private void handleConnection(Socket socket) {
        ObjectOutputStream writer;
        ObjectInputStream reader;
        try {
            writer = new ObjectOutputStream(socket.getOutputStream());
            writer.flush();
            reader = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            logger.log(ERROR, "Could not initialize communication streams for an incoming peer connection", ex);
            return;
        }

        // Lecture du premier message := l'UUID du client
        UUID uuid;
        try {
            uuid = (UUID) reader.readObject(); 
            if (uuid == null) {
                logger.log(ERROR, "UUID sent is null. Closing connection");
                socket.close();
                return;
            }
        } catch (ClassNotFoundException ex) {
            logger.log(ERROR, "Object sent at initialization was not a UUID", ex);
            closeQuietly(socket);
            return;
        } catch (IOException ex) {
            logger.log(ERROR, "Could not read the UUID sent during peer connection initialization", ex);
            closeQuietly(socket);
            return;
        }

        // Adding streams and socket to the RemoteClient object in the manager
        // And starts the listening thread for the remoteClient
        clientController.clientConnectionsManager.receiveConnectionFromClient(uuid, socket, reader, writer);
    }

    // Closing the listening socket
    public void stopListening() {
        if (listeningSocket == null) {
            return; // the service was never started
        }
        try {
            listeningSocket.close();
        } catch (IOException ex) {
            logger.log(ERROR, "Error while closing the listening socket:", ex);
        }
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
