package common.interfaces;

import java.util.UUID;

public interface InterfaceIHMMain_calls_Comm {
    void closeSession();

    void askDelete(UUID video_id);
}
