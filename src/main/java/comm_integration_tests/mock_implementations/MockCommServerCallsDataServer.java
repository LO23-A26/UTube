package comm_integration_tests.mock_implementations;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceCommServerCallsDataServer;

public class MockCommServerCallsDataServer implements InterfaceCommServerCallsDataServer {

    @Override
    public ArrayList<Object> registerUserAndGetIndex(UserLight new_user, Set<VideoLight> new_videos) {
        System.out.println("[MockCommServerCallsDataServer] registerUserAndGetIndex: " + new_user + " / " + new_videos.size());
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
