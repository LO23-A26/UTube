package client.data;

import common.dataclasses.UserLight;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of the IHM Share calls Dataclient interface.
 *
 * @author Florian Goutard
 * @version 1.0
 */
public class IhmShareCallsDataClientImpl implements InterfaceIhmShareCallsDataClient {

    private final DataClientCore data_client_core;

    public IhmShareCallsDataClientImpl(DataClientCore data_client_core) {
        this.data_client_core = data_client_core;
    }

    /**
     * Sends a request for the details of a video to its owner.
     *
     * @param video_id The UUID of the video.
     */
    @Override
    public void sendDetailsRequest(UUID video_id) {
        UserLight owner_profile = findOwner(video_id);
        if (owner_profile == null) {
            System.out.println("sendDetailsRequest - Unknown video.");
            return;
        }
        UserLight requester_profile = data_client_core.getUser().getSharedInfo();
        data_client_core.getInterfaceFromComm().giveDetailsRequest(requester_profile, owner_profile, video_id);
    }

    /**
     * Finds the owner of a video in the local catalog.
     *
     * @param video_id The UUID of the video.
     * @return The owner of the video, or null if the video is unknown.
     */
    private UserLight findOwner(UUID video_id) {
        Set<VideoLight> videos = data_client_core.getListVideo();
        if (videos == null) {
            return null;
        }
        for (VideoLight video : videos) {
            if (video.getVideoId().equals(video_id)) {
                return video.getOwner();
            }
        }
        return null;
    }

    /**
     * Retrieves the current user's light profile.
     *
     * @return UserLight object.
     */
    @Override
    public UserLight getUser() {
        return data_client_core.getUser().getSharedInfo();
    }

    /**
     * Adds a new video with metadata. Not implemented yet (upload).
     *
     * @param file The file path of the video.
     * @param title The title of the video.
     * @param creator The name of the video's creator.
     * @param tags A set of tags for the video.
     */
    @Override
    public void addVideo(Path file, String title, String creator, Set<String> tags) {
        data_client_core.addingNewVideo(file, title, creator, tags);
    }

    /**
     * Gets the full version of a video stored on this computer.
     * For now, only the videos uploaded by the user are stored locally.
     *
     * @param video The lightweight video object.
     * @return The local VideoFull, or null if the video is not stored on this computer.
     */
    @Override
    public VideoFull getVideo(VideoLight video) {
        if (video == null || data_client_core.getUser() == null) {
            return null;
        }
        Map<UUID, VideoFull> uploaded_videos = data_client_core.getUser().getUploadedVideos();
        if (uploaded_videos == null) {
            return null;
        }
        return uploaded_videos.get(video.getVideoId());
    }

    /**
     * Deletes a comment from a video. Not implemented yet.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the author.
     */
    @Override
    public void deleteComment(UUID video_id, UUID author_uid) {
    }

    /**
     * Deletes a rating from a video. Not implemented yet.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the author.
     */
    @Override
    public void deleteRating(UUID video_id, UUID author_uid) {
    }

    /**
     * Gets the Data client core used by this implementation.
     *
     * @return The Data client core.
     */
    public DataClientCore getDataClientCore() {
        return data_client_core;
    }

}
