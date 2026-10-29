package server.data;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceCommServerCallsDataServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CommServerCallsDataServerImpl implements InterfaceCommServerCallsDataServer {

    private DataServerCore data_server_core;

    public CommServerCallsDataServerImpl() {
    }

    public CommServerCallsDataServerImpl(DataServerCore data_server_core) {
        this.data_server_core = data_server_core;
    }

    public DataServerCore getDataServerCore() {
        return data_server_core;
    }

    public void setDataServerCore(DataServerCore data_server_core) {
        this.data_server_core = data_server_core;
    }

    @Override
    public ArrayList<Object> registerUserAndGetIndex(UserLight new_user, Set<VideoLight> new_videos) {
        if (data_server_core != null) {
            return data_server_core.registerUserAndGetIndex(new_user, new_videos);
        }
        return new ArrayList<>();
    }

    @Override
    public void removeUser(UUID user_id) {
    }

    @Override
    public void deleteIndex(UUID video_id) {
    }

    @Override
    public void updateIndex(VideoLight video_info) {
    }

    @Override
    public List<UserLight> getConnectedClients() {
        if (data_server_core != null) {
            return data_server_core.getUserList();
        }
        return List.of();
    }
}
