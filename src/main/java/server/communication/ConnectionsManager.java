package server.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import client.communication.RemoteClient;
import common.dataclasses.communication.ClientMessage;

public class ConnectionsManager {

    // map of clients by uuid
    private final Map<UUID, ActiveClient> clients;

    public ConnectionsManager() {
        this.clients = new ConcurrentHashMap<>();
    }
    /**
     * Registers a newly connected client.
     *
     * @param client the client to register; ignored if null.
     */
    public void connectNewClient(ActiveClient client) {
        if (client == null) {
            return;
        }
        clients.put(client.getUuid(), client);
    }

    /**
     * Sends a message to one client, identified by its UUID.
     * Does nothing if no such client is connected.
     *
     * @param uuid    the UUID of the recipient.
     * @param message the message to send.
     */
    public void sendMessageToClient(UUID uuid, ClientMessage message) {
        ActiveClient client = clients.get(uuid);
        if (client == null) {
            return;
        }

        try {
            client.send(message);
        } catch (IOException e) {
            client.close();
        }
    }
    /**
     * Sends a message to every connected client except the one that triggered the event.
     * The initiator already knows about the change, so notifying it would only
     * duplicate the information on its side.
     *
     * @param excludedUuid the UUID of the client that initiated the event, which must not receive the message.
     * @param message      the message to send.
     */
    public void broadcastMessageExcept(UUID excludedUuid, ClientMessage message) {
        for (ActiveClient client : clients.values()) {
            if (client.getUuid().equals(excludedUuid)) {
                continue;
            }
            try {
                client.send(message);
            } catch (IOException e) {
                client.close();
            }
        }
    }

    public void broadcastMessage(ClientMessage message) {
        for (ActiveClient client : clients.values()) {
            try {
                client.send(message);
            } catch (IOException e) {
                client.close();
            }
        }
    }

    /** @return true if this call actually removed the client. */
    public boolean disconnectClient(ActiveClient client) {
        if (client == null) {
            return false;
        }
        boolean removed = clients.remove(client.getUuid()) != null;
        client.close();
        return removed;
    }

    /**
     * Gives the other clients which can be contacted directly: those which announced where they listen for the
     * other clients. A client which did not announce it (yet) is skipped.
     *
     * @param excludedUuid UUID of the client asking
     * @return a copy of the list
     */
    public List<RemoteClient> getRemoteClientsExcept(UUID excludedUuid) {
        List<RemoteClient> others = new ArrayList<>();
        for (ActiveClient client : clients.values()) {
            RemoteClient remote = client.getRemoteClient();
            if (remote != null && !client.getUuid().equals(excludedUuid)) {
                others.add(remote);
            }
        }
        return others;
    }

}
