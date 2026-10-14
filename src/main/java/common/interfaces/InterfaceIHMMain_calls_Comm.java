package common.interfaces;

import java.util.UUID;

public interface InterfaceIHMMain_calls_Comm {
    void closeSession();

    void requestConnection(user_profile UserLight, password String, ip_address String, port int);
    
    void askDelete(UUID video_id);
}
