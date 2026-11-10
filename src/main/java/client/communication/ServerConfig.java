package client.communication;

import java.net.InetAddress;

public class ServerConfig {
    private InetAddress ip;
    private int port;

    public ServerConfig(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // getters and setters

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
