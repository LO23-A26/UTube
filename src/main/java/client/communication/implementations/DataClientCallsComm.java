package client.communication.implementations;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import client.communication.CommClientController;
import client.communication.ServerConfig;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ServerMessage;
import common.dataclasses.communication.messages.ConnectionRequestMessage;
import common.interfaces.comm.InterfaceDataCallsComm;

/**
 * Implementation of the InterfaceDataCallsComm interface.
 * This class is responsible for handling communication calls related to the DataClient interface.
 */
public class DataClientCallsComm implements InterfaceDataCallsComm {

    private final CommClientController commClientController;

    // Constructor, takes in the comm client controller
    public DataClientCallsComm(CommClientController commClientController) {
        this.commClientController = commClientController;
    }

    @Override
    public void sendProfileInfo(String ip_address, int port, List<VideoLight> video_list, String pseudo) { // Should send a video Set instead
        // Create hard coded config for now
        InetAddress server_ip;
        try {
            server_ip = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException ex) {
            System.getLogger(DataClientCallsComm.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return;
        }
        ServerConfig config = new ServerConfig(server_ip, 9000);
        
        // creates socket with server and sends UUID
        commClientController.initializeServerConnexion(config);
        // Send profile to server
        ServerMessage message = new ConnectionRequestMessage( 
            commClientController.interfaceCommDataClient.getMyProfile().getSharedInfo(),
            ip_address,
            port,
            new HashSet<>(video_list)  // Temporary conversion waiting for parameter type to change
        );
        commClientController.serverConnection.sendMessage(message); 
    }

    @Override
    public void sendNewVideo(VideoLight video_info) {
        throw new UnsupportedOperationException("Unimplemented method 'sendNewVideo'");
    }

    @Override
    public void giveDetailsRequest(UserLight requester_profile, UserLight owner_profile, UUID video) {
        throw new UnsupportedOperationException("Unimplemented method 'giveDetailsRequest'");
    }
    
}
