package common.interfaces.comm;

import java.util.List;

import common.dataclasses.UserLight;

public interface InterfaceDataServer_calls_CommServer {
    void sendListVideo(List<UserLight> list_user);
}
