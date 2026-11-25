package client.communication.implementations;

import java.io.IOException;
import static java.lang.System.Logger.Level.ERROR;
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

    private static final System.Logger logger = System.getLogger(DataClientCallsComm.class.getName());

    private final CommClientController commClientController;

    // Constructor, takes in the comm client controller
    public DataClientCallsComm(CommClientController commClientController) {
        this.commClientController = commClientController;
    }

    // connects the client to the server and send its profile
    @Override
    public void sendProfileInfo(String ip_address, int port, List<VideoLight> video_list, String pseudo) { // Should send a video Set instead
        // listening for the other clients first so that we know the port to announce to the server
        int listeningPort;
        try {
            listeningPort = commClientController.startClientListeningService();
        } catch (IOException ex) {
            logger.log(ERROR, "Cannot listen for the other clients: ", ex);
            return;
        }

        InetAddress server_ip;
        try {
            server_ip = InetAddress.getByName(ip_address);
        } catch (UnknownHostException ex) {
            logger.log(ERROR, "Ip invalide", ex);
            return;
        }
        ServerConfig config = new ServerConfig(server_ip, port);
        
        // creates socket with server and sends UUID
        commClientController.initializeServerConnexion(config);
        // Send profile to server with the port on which the other clients can reach
        ServerMessage message = new ConnectionRequestMessage( 
            commClientController.interfaceCommDataClient.getMyProfile().getSharedInfo(),
            listeningPort,
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
        commClientController.requestVideoDetailsFromClient(requester_profile, owner_profile, video);
    }
    
}
