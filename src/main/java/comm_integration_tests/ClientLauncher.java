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

public class ClientLauncher {

    public static void main(String[] args) throws InterruptedException {
        String name = args.length > 0 ? args[0] : "John Doe";
        int port    = args.length > 1 ? Integer.parseInt(args[1]) : 9999;

        System.out.println("[ClientLauncher:" + name + "] Starting on port " + port);

        CommClientController controller = new CommClientController();
        controller.setInterfaceCommIHMShare(new MockCommunicationCallsIHMShare());
        controller.setInterfaceCommDataClient(new MockCommClientCallsDataClient());

        // Propriétaire fictif des vidéos (constructeur à adapter à UserLight)
        UserLight owner = new UserLight(UUID.randomUUID(), name, null);

        List<VideoLight> videos = new ArrayList<>();
        videos.add(new VideoLight(owner, name + " - video 1", name, Set.of("test", "comm")));
        videos.add(new VideoLight(owner, name + " - video 2", name, Set.of("test")));

        controller.getDataCallsComm().sendProfileInfo("127.0.0.1", port, videos, name);

        Thread.currentThread().join();
    }
}