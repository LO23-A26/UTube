package common.interfaces.data;

import common.dataclasses.*;
import java.util.UUID;
import java.util.List;

/**
 * Interface for communication client calls to the data client.
 * Provides methods for video access, user management, and rating/commenting.
 */
public interface InterfaceCommClientCallsDataClient {

    /**
     * Gives details access for a specific video to a requester.
     *
     * @param requester_profile The profile of the user requesting access.
     * @param video_id The UUID of the video.
     * @return VideoHeavy object containing heavy video details, or null by default.
     */
    public VideoFull giveDetailsAccess(UserLight requester_profile, UUID video_id);

    /**
     * Adds a comment to a video.
     *
     * @param comment The comment object to be added.
     */
    public void addComment(Comment comment);

    /**
     * Edits an existing comment.
     *
     * @param comment The comment object containing updated data.
     */
    public void editComment(Comment comment);

    /**
     * Adds a rating to a video.
     *
     * @param rating The rating object to be added.
     */
    public void addRating(Rating rating);

    /**
     * Edits an existing rating.
     *
     * @param rating The rating object containing updated data.
     */
    public void editRating(Rating rating);

    /**
     * Deletes a comment from a video based on video ID and author ID.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the comment's author.
     */
    public void deleteComment(UUID video_id, UUID author_uid);

    /**
     * Deletes a rating from a video.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the rating's author.
     */
    public void deleteRating(UUID video_id, UUID author_uid);

    /**
     * Marks a video as shared.
     *
     * @param video_info The lightweight video information object.
     */
    public void markAsShared(VideoLight video_info);

    /**
     * Adds a remote video to the system.
     *
     * @param video_info The lightweight video information object.
     */
    public void addRemoteVideo(VideoLight video_info);

    /**
     * Removes a video from a user's collection.
     *
     * @param video_id The UUID of the video to remove.
     */
    public void removeVideoFromUser(UUID video_id);

    /**
     * Asks for the requester's ID.
     *
     * @return UUID representing the requester.
     */
    public UUID askRequesterId();

    /**
     * Requests a video's data.
     *
     * @param video_id The UUID of the video.
     * @param requester_id The UUID of the user requesting the video.
     * @return Byte array containing video data.
     */
    public byte[] askVideo(UUID video_id, UUID requester_id);

    /**
     * Saves a video file to the system.
     *
     * @param video_file The byte array of the video file data.
     * @return VideoLight object representing the saved video.
     */
    public VideoLight saveVideo(byte[] video_file);

    /**
     * Saves a new user and their associated new video.
     *
     * @param new_user The lightweight user profile.
     * @param new_video The lightweight video information.
     */
    public void saveNewUser(UserLight new_user, VideoLight new_video);

    /**
     * Saves a full index of users and videos.
     *
     * @param user_list The list of user profiles.
     * @param video_list The list of videos.
     */
    public void saveFullIndex(List<UserLight> user_list, List<VideoLight> video_list);

    /**
     * Retrieves the current user's full profile.
     *
     * @return UserFull object representing the current user.
     */
    public UserFull getMyProfile();

    /**
     * Saves an updated user profile.
     *
     * @param user_profile The updated full user profile.
     */
    public void saveProfileUpdated(UserFull user_profile);

    /**
     * Removes a user from the system.
     *
     * @param user_profile The full user profile to remove.
     */
    public void removeUser(UserFull user_profile);

    /**
     * Removes a shared video.
     *
     * @param video_id The UUID of the video to remove.
     */
    public void removeSharedVideo(UUID video_id);
}
