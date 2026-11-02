package client.communication;

import java.net.InetAddress;

public class ServerConfig {
    private InetAddress IP;
    private int port;

    // getters and setters
    public InetAddress getIP() {
        return IP;
    }

    public void setIP(InetAddress IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
