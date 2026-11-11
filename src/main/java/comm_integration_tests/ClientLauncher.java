package comm_integration_tests;

import java.util.ArrayList;

import client.communication.CommClientController;
import comm_integration_tests.mock_implementations.MockCommClientCallsDataClient;
import comm_integration_tests.mock_implementations.MockCommunicationCallsIHMShare;
import common.dataclasses.VideoLight;

public class ClientLauncher {

    public static void main(String[] args) {
        System.out.println("[ClientLauncher] Starting communication client with mocked external interfaces...");

        CommClientController controller = new CommClientController();
        controller.setInterfaceCommIHMShare(new MockCommunicationCallsIHMShare());
        controller.setInterfaceCommDataClient(new MockCommClientCallsDataClient());

        //controller.run();
        controller.getDataCallsComm().sendProfileInfo("127.0.0.1", 9999, new ArrayList<VideoLight>(), "John Doe");
    }
}
