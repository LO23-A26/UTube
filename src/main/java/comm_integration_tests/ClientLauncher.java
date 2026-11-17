package comm_integration_tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import client.communication.CommClientController;
import comm_integration_tests.mock_implementations.MockCommClientCallsDataClient;
import comm_integration_tests.mock_implementations.MockCommunicationCallsIHMShare;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.messages.DeleteVideoMessage;
import common.dataclasses.communication.messages.ShareVideoMessage;

public class ClientLauncher {

    public static void main(String[] args) throws InterruptedException {
        String name = args.length > 0 ? args[0] : "John Doe";
        int port    = args.length > 1 ? Integer.parseInt(args[1]) : 9999;
        String mode = args.length > 2 ? args[2] : "";

        System.out.println("[ClientLauncher:" + name + "] Starting on port " + port);

        CommClientController controller = new CommClientController();
        controller.setInterfaceCommIHMShare(new MockCommunicationCallsIHMShare());
        controller.setInterfaceCommDataClient(new MockCommClientCallsDataClient());

        UserLight owner = new UserLight(UUID.randomUUID(), name, null);
        List<VideoLight> videos = new ArrayList<>();
        videos.add(new VideoLight(owner, name + " - video 1", name, Set.of("test", "comm")));
        videos.add(new VideoLight(owner, name + " - video 2", name, Set.of("test")));

        controller.getDataCallsComm().sendProfileInfo("127.0.0.1", port, videos, name);

        if (mode.equals("share")) {
            Thread.sleep(5000); // laisser les autres clients se connecter
            VideoLight video = new VideoLight(owner, name + " - shared video", name);
            controller.serverConnection.sendMessage(new ShareVideoMessage(video));
            System.out.println("[ClientLauncher:" + name + "] ShareVideoMessage sent: " + video);
        } else if (mode.equals("delete")) {
            Thread.sleep(5000);
            UUID videoId = UUID.randomUUID();
            controller.serverConnection.sendMessage(new DeleteVideoMessage(videoId));
            System.out.println("[ClientLauncher:" + name + "] DeleteVideoMessage sent: " + videoId);
        } else if (args.length > 2 && args[2].equals("leave")) {
            Thread.sleep(5000);
            controller.serverConnection.disconnect();
            System.out.println("[ClientLauncher:" + name + "] disconnect sent");
            return;
        }

        Thread.currentThread().join();
    }
}