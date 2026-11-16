package common.dataclasses.communication.messages;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import client.communication.CommClientController;
import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;
import common.dataclasses.communication.PeerAddress;

/**
 * Sent to a new user when they connect to the server, containing the full index of users and videos,
 * and the address of the clients already connected
 */
public class SendFullIndexMessage implements ClientMessage {
    private final Index index;
    private final List<PeerAddress> peers;

    public SendFullIndexMessage(Index index, List<PeerAddress> peers) {
        this.index = index;
        this.peers = peers;
    }

    /**
     * Handles the message by remembering where the other clients listen, then giving the full index to the Data client.
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        for (PeerAddress peer : peers) {
            try {
                controller.addClient(InetAddress.getByName(peer.getIp()), peer.getPort(), peer.getUuid());
            } catch (UnknownHostException ex) {
                System.getLogger(SendFullIndexMessage.class.getName()).log(System.Logger.Level.ERROR,
                        "Address of client " + peer.getUuid() + " is invalid, it will not be reachable", ex);
            }
        }

        System.getLogger(SendFullIndexMessage.class.getName()).log(System.Logger.Level.INFO,
                "Received the address of " + peers.size() + " connected client(s)");
        List<UserLight> users = index.getUserList();
        List<VideoLight> videos = index.getVideoList();
        controller.interfaceCommDataClient.saveFullIndex(users, videos);
    }
}
