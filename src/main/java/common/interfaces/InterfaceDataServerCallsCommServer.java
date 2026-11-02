package common.interfaces;

import common.dataclasses.UserLight;

import java.util.List;

public interface InterfaceDataServerCallsCommServer {
    void sendListVideo(List<UserLight> list_user);
}
