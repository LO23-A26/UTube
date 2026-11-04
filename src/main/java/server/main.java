package server;

import server.communication.ConnectionsManager;
import server.communication.NewConnectionListeningThread;

public class main {

    public static void main(String[] args) {
        ConnectionsManager connectionsManager = new ConnectionsManager();
        new NewConnectionListeningThread(connectionsManager).start();
        System.out.println("listening on port 9000");
    }
}
