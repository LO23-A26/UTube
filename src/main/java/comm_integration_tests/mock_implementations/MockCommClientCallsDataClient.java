package comm_integration_tests.mock_implementations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoFormat;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceCommClientCallsDataClient;

public class MockCommClientCallsDataClient implements InterfaceCommClientCallsDataClient {
    private final UUID fixedRequesterUuid = UUID.randomUUID();

    private final String pseudo;
    // users received in the index by UUID
    private final Map<UUID, String> knownUsers = new ConcurrentHashMap<>();

    public MockCommClientCallsDataClient() {
        this("Jhon Doe");
    }

    public MockCommClientCallsDataClient(String pseudo) {
        this.pseudo = pseudo;
    }

    public Map<UUID, String> getKnownUsers() {
        return knownUsers;
    }

    public UUID getMyUuid() {
        return fixedRequesterUuid;
    }

    @Override
    public VideoFull giveDetailsAccess(UserLight requester_profile, UUID video_id) {
        System.out.println("[MockCommClientCallsDataClient] giveDetailsAccess for video " + video_id
                + ", asked by " + requester_profile);
        UserLight owner = new UserLight(fixedRequesterUuid, pseudo, null);
        return new VideoFull(owner, pseudo + " - details of video " + video_id, pseudo, Set.of("test"),
                "videos/" + video_id + ".mp4", VideoFormat.mp4);
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
        return fixedRequesterUuid;
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
    public void saveFullIndex(List<UserLight> users, List<VideoLight> videos) {
        System.out.println("[MockDataClient] saveFullIndex: " + users.size()
                + " users, " + videos.size() + " videos");
        users.forEach(u -> System.out.println("   - " + u));
        users.forEach(u -> knownUsers.put(u.getUuid(), u.getPseudo()));
    }

    @Override
    public void saveNewUser(UserLight user, List<VideoLight> videos) {
        System.out.println("[MockDataClient] saveNewUser: " + user
                + " (" + (videos == null ? 0 : videos.size()) + " videos)");
        knownUsers.put(user.getUuid(), user.getPseudo());
    }

    @Override
    public UserFull getMyProfile() {
        System.out.println("[MockCommClientCallsDataClient] getMyProfile");

        //On crée le profil normalement
        UserFull profile = new UserFull(pseudo, "kzjhebfkjah", null, null, null);

        //On remplace simplement l'UUID généré par notre UUID fixe
        profile.getSharedInfo().setUuid(fixedRequesterUuid);

        return profile;
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
