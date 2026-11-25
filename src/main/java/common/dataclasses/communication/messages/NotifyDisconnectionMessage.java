package common.dataclasses.communication.messages;

import static java.lang.System.Logger.Level.INFO;
import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.communication.ClientMessage;

/** Tells a client that a user has disconnected. */
public class NotifyDisconnectionMessage implements ClientMessage {

    private static transient final System.Logger logger = System.getLogger(NotifyDisconnectionMessage.class.getName());
    private final UUID userId;

    public NotifyDisconnectionMessage(UUID userId) {
        this.userId = userId;
    }

    @Override
    public void handle(CommClientController controller) {
        // this client can no longer be contacted directly
        controller.clientConnectionsManager.removeClient(userId);
        controller.interfaceCommDataClient.removeUser(userId);
        // TODO: remove the user (and their videos) from the local index through the
        // data client, once the data team has provided a method taking a UUID.
        logger.log(INFO, "user disconnected: {0}", userId);   // TEMP
    }
}