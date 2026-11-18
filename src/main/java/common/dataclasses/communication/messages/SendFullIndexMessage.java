package common.dataclasses.communication.messages;
import java.util.List;

import client.communication.CommClientController;
import client.communication.RemoteClient;
import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;

/**
 * Sent to a new user when they connect to the server, containing the full index of users and videos,
 * and the clients already connected, so that this one can contact them directly
 */
public class SendFullIndexMessage implements ClientMessage {
    private final Index index;
    private final List<RemoteClient> peers;

    public SendFullIndexMessage(Index index, List<RemoteClient> peers) {
        this.index = index;
        this.peers = peers;
    }

    /**
     * Handles the message by remembering where the other clients listen, then giving the full index to the Data client.
     * @param controller The client controller.
     */
    @Override
    public void handle(CommClientController controller) {
        for (RemoteClient peer : peers) {
            controller.addClient(peer.getIP(), peer.getPort(), peer.getUuid());
        }

        System.getLogger(SendFullIndexMessage.class.getName()).log(System.Logger.Level.DEBUG,
                "Received the address of " + peers.size() + " connected client(s)");
        List<UserLight> users = index.getUserList();
        List<VideoLight> videos = index.getVideoList();
        controller.interfaceCommDataClient.saveFullIndex(users, videos);
    }
}
