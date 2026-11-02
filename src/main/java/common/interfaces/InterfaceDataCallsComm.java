package common.interfaces;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;

import java.util.List;
import java.util.UUID;
import java.net.InetAddress;

public interface InterfaceDataCallsComm {
    void sendProfileInfo(InetAddress ip, int port, List<VideoLight> video_list, String pseudo);

    void sendNewVideo(VideoLight video_info);

    void giveDetailsRequest(UserLight requester_profile, UserLight owner_profile, UUID video);
}
