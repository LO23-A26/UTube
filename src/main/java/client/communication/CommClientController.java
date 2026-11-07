package client.communication;

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

import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.data.InterfaceCommClientCallsDataClient;

public class CommClientController {
    private ClientListeningService clientListener;
    private ServerListeningService  serverListener;
    private ServerConnection serverConnection;
    private HashMap<UUID,ActiveClient> connectedClients;

    private InterfaceCommunicationCallsIHMShare interfaceCommIHMShare;
    public InterfaceCommClientCallsDataClient interfaceCommDataClient;

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("localhost", 9000), 2_000);
            socket.setSoTimeout(5_000);

            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // Envoi de l'UUID dès l'établissement de la connexion
            UUID clientUuid = UUID.randomUUID(); //!!! Mock UUID for testing purposes

            out.write(clientUuid.toString());
            out.newLine();
            out.flush();

            String response = in.readLine();
            System.out.println(response);
        }
    }
}
