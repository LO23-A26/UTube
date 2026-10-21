package common.interfaces.comm;

import java.util.UUID;

import common.dataclasses.UserLight;

public interface InterfaceIHMMain_calls_Comm {
    void closeSession();

    void requestConnection(UserLight user_profile , String ip_address, int port);
    
    void askDelete(UUID video_id);
}
