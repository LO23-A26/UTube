package comm_integration_tests;

import comm_integration_tests.mock_implementations.MockCommServerCallsDataServer;
import server.communication.CommServerController;

public class ServerLauncher {

    public static void main(String[] args) {
        System.out.println("[ServerLauncher] Starting communication server with mocked external interface...");

        CommServerController controller = new CommServerController();
        controller.setInterfaceCommServerCallsDataServer(new MockCommServerCallsDataServer());
        controller.start();

        while (true) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
