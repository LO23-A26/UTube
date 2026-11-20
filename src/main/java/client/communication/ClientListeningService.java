package client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.Logger.Level.ERROR;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.dataclasses.communication.ClientMessage;

/**
 * Service that listens for peer-to-peer connections from the other clients
 * Gives the client its own listening port on which the other clients can connect directly
 */
public class ClientListeningService extends Thread {
    private final CommClientController clientController; 
    private ServerSocket listeningSocket;
    private static final System.Logger logger = System.getLogger(ClientListeningService.class.getName());

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
        System.out.println("[ClientListeningService] Listening for the other clients on port " + getPort());
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
        } catch (SocketException e) {
            System.out.println("[ClientListeningService] Stopped listening for the other clients");
        } catch (IOException e) {
            System.err.println("[ClientListeningService] Listening stopped unexpectedly: " + e.getMessage());
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
            logger.log(ERROR, (String) null, ex);
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
            logger.log(ERROR, "null", ex);
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
        } catch (IOException e) {
            System.err.println("[ClientListeningService] Error while closing the listening socket: " + e.getMessage());
        }
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}
