package common.dataclasses.communication;

import client.communication.CommClientController;

/**
 * An interface for messages send TO the client, from the server or another client.
 */
public interface ClientMessage extends java.io.Serializable {

    /**
     * Handles the message by performing the appropriate action on the client side.
     * @param controller The client controller.
     */
    public void handle(CommClientController controller);
}
