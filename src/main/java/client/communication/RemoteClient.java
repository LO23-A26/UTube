package client.communication;

import java.io.Serializable;
import java.util.UUID;
import java.net.InetAddress;

public class RemoteClient implements Serializable { // Other clients connected to the server
    private final UUID uuid;
    private final InetAddress ip;
    private final int port;

    /**
     * @param ip   the IP address of the client
     * @param port the port on which the client listens for the other clients
     * @param uuid the UUID of the user of the client
     */
    public RemoteClient(InetAddress ip, int port, UUID uuid) {
        this.ip = ip;
        this.port = port;
        this.uuid = uuid;
    }

    /** @return the UUID of the user of the client */
    public UUID getUuid() {
        return uuid;
    }

    /** @return the IP address of the client */
    public InetAddress getIP() {
        return ip;
    }

    /** @return the port on which the client listens for the other clients */
    public int getPort() {
        return port;
    }
}
