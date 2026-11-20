package client.communication;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import common.dataclasses.communication.ClientMessage;

/**
 * The other clients connected (P2P)
 */
public class ClientConnectionsManager {

    // available clients by their user uuid
    private final Map<UUID, RemoteClient> availableClients;
    private final CommClientController clientController;

    public ClientConnectionsManager(CommClientController clientController) {
        this.availableClients = new ConcurrentHashMap<>();
        this.clientController = clientController;
    }

    public ClientConnectionsManager() {
        this(null);
    }

    /**
     * Remembers a client who is connected to the server
     *
     * @param client the client to remember
     */
    public void addClient(RemoteClient client) {
        if (client == null || client.getUuid() == null || client.getIP() == null) {
            return;
        }
        client.setClientController(clientController);
        availableClients.put(client.getUuid(), client);
    }

    /**
     * Forgets a client who left
     *
     * @param uuid the UUID of the client to forget
     * @return true if the client was known, that is if this call actually forgot it
     */
    public boolean removeClient(UUID uuid) {
        if (uuid == null) {
            return false;
        }

        // if client is connected, disconnect it first
        RemoteClient client = availableClients.get(uuid);
        if (client != null && client.isConnected()) {
            client.disconnect();
        }

        return availableClients.remove(uuid) != null;
    }

    /**
     * Gets a client, to contact it directly.
     *
     * @param uuid the UUID of the client
     * @return the client, or null if it is not known (it is not connected, or its address was not announced)
     */
    private RemoteClient getClient(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        return availableClients.get(uuid);
    }

    /**
     * Returns a string representation of the client, for logging purposes.
     * For now just the IP address and port, or "forgotten" if the client is not known.
     * @param uuid
     * @return
     */
    public String getClientString(UUID uuid) {
        RemoteClient remote = this.getClient(uuid);
        return (remote == null
                ? "forgotten"
                : remote.getIP().getHostAddress() + ":" + remote.getPort());
    }

    /**
     * Opens a connection to a remote client, if it is known and not already connected.
     * @param client the remote client to connect to
     */
    public void openConnectionToClient(UUID uuid) {
        RemoteClient client = this.getClient(uuid);
        if (client == null) {
            throw new IllegalArgumentException("Client with UUID " + uuid + " is not known");
        }
        if (client.isConnected()) {
            return;
        }

        try {
            Socket socket = new Socket(client.getIP(), client.getPort());
            client.initiateConnection(socket);
        } catch (IOException e) {
            System.err.println("[ClientConnectionsManager] Could not open connection to "
                    + client.getIP().getHostAddress() + ":" + client.getPort()
                    + ": " + e.getMessage());
        }
    }

    /**
     * Sends a message to a remote client, if it is known and connected.
     * @param client the remote client to send the message to
     * @param message the message to send
     */
    public void sendToClient(UUID uuid, ClientMessage message) {
        
        RemoteClient client = this.getClient(uuid);
        if (client == null || message == null) {
            return;
        }
        client.sendMessage(message);
    }
}
