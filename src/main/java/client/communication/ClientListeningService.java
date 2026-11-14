package client.communication;

import java.net.ServerSocket;

/**
 * Service that listens for incoming connections from another client.
 * TODO: Implementation
 */
public class ClientListeningService extends Thread {
    @SuppressWarnings("unused")
    private final CommClientController clientController;
    private ServerSocket listeningSocket;

    // Constructor
    public ClientListeningService(CommClientController clientController) {
        this.clientController = clientController;
    }

    public ServerSocket getListeningSocket() {
        return listeningSocket;
    }

    public void setListeningSocket(ServerSocket listeningSocket) {
        this.listeningSocket = listeningSocket;
    }

    // thread execution
    @Override
    public void run(){
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
