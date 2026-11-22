package common.dataclasses.communication;

import server.communication.CommServerController;

/**
 * A message sent TO the server, from a client.
 */
public interface ServerMessage extends java.io.Serializable {

    /**
     * Handles the message by performing the appropriate action on the server side.
     * @param controller The server controller.
     */
    public void handle(CommServerController controller);

}
