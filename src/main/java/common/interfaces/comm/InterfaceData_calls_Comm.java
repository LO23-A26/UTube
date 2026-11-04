package common.interfaces.comm;

import java.util.List;
import java.util.UUID;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;

public interface InterfaceData_calls_Comm {
    void sendProfileInfo(String ip_address, int port, List<VideoLight> video_list, String pseudo);

    void sendNewVideo(VideoLight video_info);

    void giveDetailsRequest(UserLight requester_profile, UserLight owner_profile, UUID video);
}
