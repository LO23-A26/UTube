package client.communication;

import java.util.UUID;
import java.net.InetAddress;

public class ActiveClient {
    private UUID uuid;
    private InetAddress ip;
    private int port;

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