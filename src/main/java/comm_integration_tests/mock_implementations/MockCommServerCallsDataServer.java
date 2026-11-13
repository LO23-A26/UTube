package comm_integration_tests.mock_implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import common.dataclasses.Index;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceCommServerCallsDataServer;

public class MockCommServerCallsDataServer implements InterfaceCommServerCallsDataServer {

    private final Index index = new Index();

    @Override
    public synchronized Index registerUserAndGetIndexStructure(UserLight new_user, Set<VideoLight> new_videos) {
        System.out.println("[MockCommServerCallsDataServer] registerUserAndGetIndexStructure: "
                + new_user + " / " + (new_videos == null ? 0 : new_videos.size()) + " videos");

        index.addUser(new_user);
        if (new_videos != null) {
            for (VideoLight video : new_videos) {
                index.addVideo(video);
            }
        }
        return index;
    }

    @Override
    public ArrayList<Object> registerUserAndGetIndex(UserLight new_user, Set<VideoLight> new_videos) {
        return new ArrayList<>();
    }

    @Override
    public void removeUser(UUID user_id) {
        System.out.println("[MockCommServerCallsDataServer] removeUser: " + user_id);
    }

    @Override
    public void deleteIndex(UUID video_id) {
        System.out.println("[MockCommServerCallsDataServer] deleteIndex: " + video_id);
    }

    @Override
    public void updateIndex(VideoLight video_info) {
        System.out.println("[MockCommServerCallsDataServer] updateIndex: " + video_info);
    }

    @Override
    public List<UserLight> getConnectedClients() {
        System.out.println("[MockCommServerCallsDataServer] getConnectedClients");
        return List.of();
    }
}
