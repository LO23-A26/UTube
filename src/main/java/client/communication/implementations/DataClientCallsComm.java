package client.communication.implementations;

import java.util.List;
import java.util.UUID;

import client.communication.CommClientController;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.comm.InterfaceDataCallsComm;

/**
 * Implementation of the InterfaceDataCallsComm interface.
 * This class is responsible for handling communication calls related to the DataClient interface.
 */
public class DataClientCallsComm implements InterfaceDataCallsComm {

    private CommClientController commClientController;

    // Constructor, takes in the comm client controller
    public DataClientCallsComm(CommClientController commClientController) {
        this.commClientController = commClientController;
    }

    @Override
    public void sendProfileInfo(String ip_address, int port, List<VideoLight> video_list, String pseudo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendProfileInfo'");
    }

    @Override
    public void sendNewVideo(VideoLight video_info) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendNewVideo'");
    }

    @Override
    public void giveDetailsRequest(UserLight requester_profile, UserLight owner_profile, UUID video) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveDetailsRequest'");
    }
    
}
