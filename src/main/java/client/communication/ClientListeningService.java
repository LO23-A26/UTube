package client.communication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Service that listens for peer-to-peer connections from the other clients
 * Gives the client its own listening port on which the other clients can connect directly
 */
public class ClientListeningService extends Thread {
    private final CommClientController clientController; // used later to answer the video requests
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

    private void handleConnection(Socket socket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Object message = in.readObject();
            System.out.println("[ClientListeningService] Received " + message.getClass().getSimpleName()
                    + " from another client (" + socket.getInetAddress().getHostAddress() + ")");
            // TODO (#88): answer the video requests
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[ClientListeningService] Error with a connection from another client: " + e.getMessage());
        } finally {
            // close connection
            try {
                socket.close();
            } catch (IOException ignored) {
                // rien
            }
        }
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

}
