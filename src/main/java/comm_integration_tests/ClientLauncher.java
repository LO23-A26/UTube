package comm_integration_tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import client.communication.CommClientController;
import client.communication.RemoteClient;
import comm_integration_tests.mock_implementations.MockCommClientCallsDataClient;
import comm_integration_tests.mock_implementations.MockCommunicationCallsIHMShare;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.messages.DeleteVideoMessage;
import common.dataclasses.communication.messages.ShareVideoMessage;

/**
 * Launches a communication client with mocked modules, to test the client-server or client-client exchanges by hand
 * Program arguments: name port [mode] [delay]
 * - name:  pseudo of the user (Anna, Bob, etc)
 * - port:  port announced to the server, where the other clients can reach this client
 * - mode:  optional action to perform: "share", "delete" or "leave"
 * - delay: optional, seconds to wait before the action, to let the other clients connect (default: 5)
 */



public class ClientLauncher {

    public static void main(String[] args) throws InterruptedException {
        String name = args.length > 0 ? args[0] : "John Doe";
        int port    = args.length > 1 ? Integer.parseInt(args[1]) : 9999;
        String mode = args.length > 2 ? args[2] : "";
        int delay   = args.length > 3 ? Integer.parseInt(args[3]) : 5; // optional: seconds to wait before the share/delete/leave action

        System.out.println("[ClientLauncher:" + name + "] Starting on port " + port);

        CommClientController controller = new CommClientController();
        controller.setInterfaceCommIHMShare(new MockCommunicationCallsIHMShare());
        // keep the mock in a variable to be able to read the list of known users later
        MockCommClientCallsDataClient dataMock = new MockCommClientCallsDataClient(name);
        controller.setInterfaceCommDataClient(dataMock);

        UserLight owner = new UserLight(UUID.randomUUID(), name, null);
        List<VideoLight> videos = new ArrayList<>();
        videos.add(new VideoLight(owner, name + " - video 1", name, Set.of("test", "comm")));
        videos.add(new VideoLight(owner, name + " - video 2", name, Set.of("test")));

        controller.getDataCallsComm().sendProfileInfo("127.0.0.1", port, videos, name);
        startDisplayingKnownClients(name, controller, dataMock);

        if (mode.equals("share")) {
            Thread.sleep(delay * 1000L); // laisser les autres clients se connecter
            VideoLight video = new VideoLight(owner, name + " - shared video", name);
            controller.serverConnection.sendMessage(new ShareVideoMessage(video));
            System.out.println("[ClientLauncher:" + name + "] ShareVideoMessage sent: " + video);
        } else if (mode.equals("delete")) {
            Thread.sleep(delay * 1000L);
            UUID videoId = UUID.randomUUID();
            controller.serverConnection.sendMessage(new DeleteVideoMessage(videoId));
            System.out.println("[ClientLauncher:" + name + "] DeleteVideoMessage sent: " + videoId);
        } else if (args.length > 2 && args[2].equals("leave")) {
            Thread.sleep(delay * 1000L);
            controller.serverConnection.disconnect();
            System.out.println("[ClientLauncher:" + name + "] disconnect sent");
            return;
        }


        Thread.currentThread().join();
    }

    /**
     * starts a small background thread which goes through the users received in the index (every 5 seconds)
     * for each of them (except the local user), asks for its addres
     * displays the IP and port if the address is known or "forgotten" otherwise
     */
    private static void startDisplayingKnownClients(String name, CommClientController controller, MockCommClientCallsDataClient dataMock) {
        Thread display = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    return;
                }
                StringBuilder text = new StringBuilder("[ClientLauncher:" + name + "] Known clients:");
                dataMock.getKnownUsers().forEach((uuid, pseudo) -> {
                    if (uuid.equals(dataMock.getMyUuid())) {
                        return;
                    }
                    RemoteClient remote = controller.getClient(uuid);
                    text.append("\n   - ").append(pseudo).append(" -> ").append(remote == null
                            ? "forgotten"
                            : remote.getIP().getHostAddress() + ":" + remote.getPort());
                });
                System.out.println(text);
            }
        });
        display.setDaemon(true);
        display.start();
    }


}