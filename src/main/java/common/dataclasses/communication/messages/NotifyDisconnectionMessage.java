package common.dataclasses.communication.messages;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.communication.ClientMessage;

/** Tells a client that a user has disconnected. */
public class NotifyDisconnectionMessage implements ClientMessage {
    private final UUID userId;

    public NotifyDisconnectionMessage(UUID userId) {
        this.userId = userId;
    }

    @Override
    public void handle(CommClientController controller) {
        // this client can no longer be contacted directly
        controller.removeClient(userId);
        // TODO: remove the user (and their videos) from the local index through the
        // data client, once the data team has provided a method taking a UUID.
        System.out.println("[Comm] user disconnected: " + userId);   // TEMP
    }
}