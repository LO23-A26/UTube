package server.communication;

import java.util.HashMap;

public class ConnectionsManager {

    // map of clients by uuid
    private HashMap<String, Client> clients;

    public ConnectionsManager() {
        this.clients = new HashMap<>();
    }

    public void connectNewClient(Client client) {
        clients.put(client.uuid, client);
    }

    public void disconnectClient(Client client) {
        clients.remove(client.uuid);
    }

    public void sendMessageToClient(String uuid, String message) {
    }

    public void broadcastMessage(String message) {
    }

}
