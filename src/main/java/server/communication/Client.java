package server.communication;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class Client {
    private CommServerController commServerController;
    private String uuid;
    private String ipAddress;
    private int port;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public Client(Socket socket) {

    }

    private static void handle(Socket socket) {
        SocketAddress remote = socket.getRemoteSocketAddress();

        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            for (String line; (line = in.readLine()) != null; ) {
                if (line.length() > 4096) {
                    out.write("ERROR: line too long");
                    out.newLine();
                    out.flush();
                    break;
                }

                out.write("echo: " + line);
                out.newLine();
                out.flush();
            }
        } catch (SocketTimeoutException timeout) {
            System.err.println("Closing idle connection from " + remote);
        } catch (IOException failure) {
            System.err.println("Connection failed for " + remote + ": " + failure.getMessage());
        }
    }

}
