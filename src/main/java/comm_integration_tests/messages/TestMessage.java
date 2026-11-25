package comm_integration_tests.messages;

import client.communication.CommClientController;
import common.dataclasses.communication.ClientMessage;

/**
 * Test message sent directly from one client to another (P2P), to check that the connection works
 */
public class TestMessage implements ClientMessage {
    private final String senderName;

    public TestMessage(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public void handle(CommClientController controller) {
        System.out.println("[TestMessage] Received a message from " + senderName);
    }
}