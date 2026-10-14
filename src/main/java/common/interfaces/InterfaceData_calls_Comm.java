package common.interfaces;

import java.util.UUID;

public interface InterfaceData_calls_Comm {
    void sendProfileInfo(String ip_address, int port, List<VideoLight> video_list, String pseudo);

    void sendNewVideo(VideoLight video_info);

    void giveDetailsRequest(UserLight requester_profile, UserLight owner_profile, UUID video);
}
