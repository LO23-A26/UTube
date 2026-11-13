package server.communication;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import common.dataclasses.communication.ClientMessage;

public class ConnectionsManager {

    // map of clients by uuid
    private final Map<UUID, ActiveClient> clients;

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

    public void sendMessageToClient(UUID uuid, ClientMessage message) {
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

    public void broadcastMessageExcept(UUID excludedUuid, ClientMessage message) {
        for (ActiveClient client : clients.values()) {
            if (client.getUuid().equals(excludedUuid)) {
                continue;
            }
            try {
                client.send(message);
            } catch (IOException e) {
                disconnectClient(client);
            }
        }
    }

}
