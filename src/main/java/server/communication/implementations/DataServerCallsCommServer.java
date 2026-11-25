package server.communication.implementations;

import java.util.List;

import common.dataclasses.UserLight;
import common.interfaces.comm.InterfaceDataServerCallsCommServer;
import server.communication.CommServerController;

/**
 * Implementation of the InterfaceDataServerCallsCommServer interface.
 * This class holds the methods that the Data server can call on the communication server.
 */
public class DataServerCallsCommServer implements InterfaceDataServerCallsCommServer {

    @SuppressWarnings("unused")
    private final CommServerController commServerController;

    // Constructor, takes in the comm server controller
    public DataServerCallsCommServer(CommServerController commServerController) {
        this.commServerController = commServerController;
    }

    @Override
    public void sendListVideo(List<UserLight> list_user) {
        throw new UnsupportedOperationException("Unimplemented method 'sendListVideo'");
    }
}
