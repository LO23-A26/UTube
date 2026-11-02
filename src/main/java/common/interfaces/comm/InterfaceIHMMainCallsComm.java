package common.interfaces;

import java.util.UUID;

public interface InterfaceIHMMainCallsComm {
    void closeSession();

    void requestConnection(user_profile UserLight, ip_address String, port int);
    
    void askDelete(UUID video_id);
}
