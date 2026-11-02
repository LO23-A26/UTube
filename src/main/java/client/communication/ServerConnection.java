package client.communication;

import java.net.Socket;

public class ServerConnection extends Thread {
    private ServerConfig config;
    private Socket serverSocket; // connection socket between client and server

    // getters and setters

    public ServerConfig getConfig() {
        return config;
    }

    public void setConfig(ServerConfig config) {
            this.config = config;
        }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }


    // independent thread execution
    @Override
    public void run() {
    }

}
