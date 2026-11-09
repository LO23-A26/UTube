package comm_integration_tests.mock_implementations;

import java.util.List;
import java.util.UUID;

import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceCommClientCallsDataClient;

public class MockCommClientCallsDataClient implements InterfaceCommClientCallsDataClient {

    @Override
    public VideoFull giveDetailsAccess(UserLight requester_profile, UUID video_id) {
        System.out.println("[MockCommClientCallsDataClient] giveDetailsAccess for video " + video_id);
        return null;
    }

    @Override
    public void addComment(Comment comment) {
        System.out.println("[MockCommClientCallsDataClient] addComment: " + comment);
    }

    @Override
    public void editComment(Comment comment) {
        System.out.println("[MockCommClientCallsDataClient] editComment: " + comment);
    }

    @Override
    public void addRating(Rating rating) {
        System.out.println("[MockCommClientCallsDataClient] addRating: " + rating);
    }

    @Override
    public void editRating(Rating rating) {
        System.out.println("[MockCommClientCallsDataClient] editRating: " + rating);
    }

    @Override
    public void deleteComment(UUID video_id, UUID author_uid) {
        System.out.println("[MockCommClientCallsDataClient] deleteComment video=" + video_id + " author=" + author_uid);
    }

    @Override
    public void deleteRating(UUID video_id, UUID author_uid) {
        System.out.println("[MockCommClientCallsDataClient] deleteRating video=" + video_id + " author=" + author_uid);
    }

    @Override
    public void markAsShared(VideoLight video_info) {
        System.out.println("[MockCommClientCallsDataClient] markAsShared: " + video_info);
    }

    @Override
    public void addRemoteVideo(VideoLight video_info) {
        System.out.println("[MockCommClientCallsDataClient] addRemoteVideo: " + video_info);
    }

    @Override
    public void removeVideoFromUser(UUID video_id) {
        System.out.println("[MockCommClientCallsDataClient] removeVideoFromUser: " + video_id);
    }

    @Override
    public UUID askRequesterId() {
        return UUID.randomUUID();
    }

    @Override
    public byte[] askVideo(UUID video_id, UUID requester_id) {
        System.out.println("[MockCommClientCallsDataClient] askVideo video=" + video_id + " requester=" + requester_id);
        return new byte[0];
    }

    @Override
    public VideoLight saveVideo(byte[] video_file) {
        System.out.println("[MockCommClientCallsDataClient] saveVideo bytes=" + video_file.length);
        return null;
    }

    @Override
    public void saveNewUser(UserLight new_user, VideoLight new_video) {
        System.out.println("[MockCommClientCallsDataClient] saveNewUser user=" + new_user + " video=" + new_video);
    }

    @Override
    public void saveFullIndex(List<UserLight> user_list, List<VideoLight> video_list) {
        System.out.println("[MockCommClientCallsDataClient] saveFullIndex users=" + user_list.size() + " videos=" + video_list.size());
    }

    @Override
    public UserFull getMyProfile() {
        System.out.println("[MockCommClientCallsDataClient] getMyProfile");
        return new UserFull("Jhon Doe", "kzjhebfkjah", null, null, null);
    }

    @Override
    public void saveProfileUpdated(UserFull user_profile) {
        System.out.println("[MockCommClientCallsDataClient] saveProfileUpdated: " + user_profile);
    }

    @Override
    public void removeUser(UserFull user_profile) {
        System.out.println("[MockCommClientCallsDataClient] removeUser: " + user_profile);
    }

    @Override
    public void removeSharedVideo(UUID video_id) {
        System.out.println("[MockCommClientCallsDataClient] removeSharedVideo: " + video_id);
    }
}
