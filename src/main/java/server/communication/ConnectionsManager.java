package server.communication;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsManager {

    // map of clients by uuid
    private final Map<String, ActiveClient> clients;

    public ConnectionsManager() {
        this.clients = new ConcurrentHashMap<>();
    }

    public void connectNewClient(ActiveClient client) {
        if (client == null) {
            return;
        }
        clients.put(client.getUuid(), client);
    }

    public void disconnectClient(ActiveClient client) {
        if (client == null) {
            return;
        }
        clients.remove(client.getUuid());
        client.close();
    }

    public void sendMessageToClient(String uuid, String message) {
        ActiveClient client = clients.get(uuid);
        if (client == null) {
            return;
        }

        try {
            client.send(message);
        } catch (IOException e) {
            disconnectClient(client);
        }
    }

    public void broadcastMessage(String message) {
        for (ActiveClient client : clients.values()) {
            try {
                client.send(message);
            } catch (IOException e) {
                disconnectClient(client);
            }
        }
    }

}
