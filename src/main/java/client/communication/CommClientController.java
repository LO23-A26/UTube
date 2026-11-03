package client.communication;

import common.interfaces.data.InterfaceCommClientCallsDataClient;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

public class CommClientController {
    private ClientListeningService clientListener;
    private ServerListeningService  serverListener;
    private ServerConnection serverConnection;
    private HashMap<UUID,ActiveClient> connectedClients;

    private InterfaceCommunicationCallsIHMShare interfaceCommIHMShare;
    public InterfaceCommClientCallsDataClient interfaceCommDataClient;

    // getters and setters

    public ClientListeningService getClientListener() {
        return clientListener;
    }

    public void setClientListener(ClientListeningService clientListener) {
        this.clientListener = clientListener;
    }

    public ServerListeningService getServerListener() {
        return serverListener;
    }

    public void setServerListener(ServerListeningService serverListener) {
        this.serverListener = serverListener;
    }

    public ServerConnection getServerConnection() {
        return serverConnection;
    }

    public void setServerConnection(ServerConnection serverConnection) {
        this.serverConnection = serverConnection;
    }

    public HashMap<UUID, ActiveClient> getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(HashMap<UUID, ActiveClient> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public InterfaceCommunicationCallsIHMShare getInterfaceCommIHMShare() {
        return interfaceCommIHMShare;
    }

    public void setInterfaceCommIHMShare(InterfaceCommunicationCallsIHMShare interfaceCommIHMShare) {
        this.interfaceCommIHMShare = interfaceCommIHMShare;
    }

    public InterfaceCommClientCallsDataClient getInterfaceCommDataClient() {
        return interfaceCommDataClient;
    }

    public void setInterfaceCommDataClient(InterfaceCommClientCallsDataClient interfaceCommDataClient) {
        this.interfaceCommDataClient = interfaceCommDataClient;
    }


    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 9000), 2_000);
            socket.setSoTimeout(5_000);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            out.write("hello");
            out.newLine();
            out.flush();

            String response = in.readLine();
            System.out.println(response);
        }
    }
}
