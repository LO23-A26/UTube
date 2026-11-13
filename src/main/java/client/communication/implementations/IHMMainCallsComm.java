package client.communication.implementations;

import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.UserLight;
import common.interfaces.comm.InterfaceIHMMainCallsComm;

/**
 * Implementation of the InterfaceCommCallsIHMMain interface.
 * This class is responsible for handling communication calls related to the IHMMain interface.
 */
public class IHMMainCallsComm implements InterfaceIHMMainCallsComm {

    @SuppressWarnings("unused")
    private final CommClientController commClientController;

    // Constructor, takes in the comm client controller
    public IHMMainCallsComm(CommClientController commClientController) {
        this.commClientController = commClientController;
    }

    @Override
    public void closeSession() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'closeSession'");
    }

    @Override
    public void requestConnection(UserLight user_profile, String ip_address, int port) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestConnection'");
    }

    @Override
    public void askDelete(UUID video_id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'askDelete'");
    }

    
}
