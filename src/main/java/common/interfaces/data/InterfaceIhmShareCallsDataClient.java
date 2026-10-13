import java.util.UUID;
import java.util.Set;
import java.nio.file.Path;


/**
 * Class for Share IHM calls to the data client.
 * Handles video sharing, details, and modifications.
 */
public class InterfaceIhmShareCallsDataClient {

    /**
     * Sends a request for video details.
     *
     * @param video_id The UUID of the video.
     */
    public void sendDetailsRequest(UUID video_id) {}

    /**
     * Retrieves the current user's light profile.
     *
     * @return UserLight object.
     */
    public UserLight getUser() {
        return null;
    }

    /**
     * Adds a new video with metadata.
     *
     * @param file The file path of the video.
     * @param title The title of the video.
     * @param creator The name of the video's creator.
     * @param tags A set of tags for the video.
     */
    public void addVideo(Path file, String title, String creator, Set<String> tags) {}

    /**
     * Gets the full video object from a lightweight representation.
     *
     * @param video The lightweight video object.
     * @return VideoFull object containing all details.
     */
    public VideoFull getVideo(VideoLight video) {
        return null;
    }

    /**
     * Deletes a comment from a video.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the author.
     */
    public void deleteComment(UUID video_id, UUID author_uid) {}

    /**
     * Deletes a rating from a video.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the author.
     */
    public void deleteRating(UUID video_id, UUID author_uid) {}
}