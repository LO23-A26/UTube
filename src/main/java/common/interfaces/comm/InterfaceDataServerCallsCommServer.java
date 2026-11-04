package common.interfaces.comm;

import java.util.List;

import common.dataclasses.UserLight;

public interface InterfaceDataServerCallsCommServer {
    void sendListVideo(List<UserLight> list_user);
}
