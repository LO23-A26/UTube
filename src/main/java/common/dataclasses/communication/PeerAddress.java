package common.dataclasses.communication;

import java.io.Serializable;
import java.util.UUID;

/**
 * Address on which a client listens for the other clients (P2P).
 *
 * The client announces it to the server when it connects, and the server gives it to the
 * other clients so they can contact each other without going through the server.
 */
public class PeerAddress implements Serializable {

    private final UUID uuid;
    private final String ip;
    private final int port;

    public PeerAddress(UUID uuid, String ip, int port) {
        this.uuid = uuid;
        this.ip = ip;
        this.port = port;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}