package common.dataclasses.communication.messages;

import common.dataclasses.communication.ServerMessage;
import server.communication.ActiveClient;
import server.communication.CommServerController;

/** Sent by a client that leaves on purpose. */
public class DisconnectionRequestMessage implements ServerMessage {
    @Override
    public void handle(CommServerController controller, ActiveClient sender) {
        controller.handleClientDisconnected(sender);
    }
}