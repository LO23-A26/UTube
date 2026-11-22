package client.communication;

import java.util.UUID;
import java.net.InetAddress;

public class ActiveClient { // Other clients connected to the server 
    private UUID uuid;
    private InetAddress ip;
    private int port;

    public ActiveClient(InetAddress ip, int port, UUID uuid) {
        this.ip = ip;
        this.port = port;
        this.uuid = uuid;
    }

    // getters and setters

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public InetAddress getIP() {
        return ip;
    }

    public void setIP(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}