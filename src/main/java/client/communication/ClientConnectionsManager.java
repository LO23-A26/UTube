package client.communication;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The other clients connected (P2P)
 */
public class ClientConnectionsManager {

    // available clients by their user uuid
    private final Map<UUID, RemoteClient> availableClients;

    public ClientConnectionsManager() {
        this.availableClients = new ConcurrentHashMap<>();
    }

    /**
     * Remembers a client who is connected to the server
     *
     * @param client the client to remember
     */
    public void connectNewClient(RemoteClient client) {
        if (client == null || client.getUuid() == null || client.getIP() == null) {
            return;
        }
        availableClients.put(client.getUuid(), client);
    }

    /**
     * Forgets a client who left
     *
     * @param uuid the UUID of the client to forget
     * @return true if the client was known, that is if this call actually forgot it
     */
    public boolean disconnectClient(UUID uuid) {
        if (uuid == null) {
            return false;
        }
        return availableClients.remove(uuid) != null;
    }

    /**
     * Gets a client, to contact it directly.
     *
     * @param uuid the UUID of the client
     * @return the client, or null if it is not known (it is not connected, or its address was not announced)
     */
    public RemoteClient getClient(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return availableClients.get(uuid);
    }
}
