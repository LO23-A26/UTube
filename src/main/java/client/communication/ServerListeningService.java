package client.communication;


import java.net.ServerSocket;

public class ServerListeningService extends Thread {
    private CommClientController clientController;
    private ServerSocket listeningSocket;

    // Constructor
    public ServerListeningService(CommClientController clientController) {
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
