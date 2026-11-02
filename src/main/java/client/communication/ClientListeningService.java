package client.communication;

import java.net.ServerSocket;

public class ClientListeningService extends Thread {
    private CommClientController clientController;
    private ServerSocket listeningSocket;

    // getters and setters

    public CommClientController getClientController() {
        return clientController;
    }

    public void setClientController(CommClientController clientController) {
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
    public void run(){}
}
