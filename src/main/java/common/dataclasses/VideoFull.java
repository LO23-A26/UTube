package common.dataclasses;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Full video data including file path, format, ratings and comments.
 *
 * @author Jean Van Gysel, Kilian Lagrevol
 * @version 2.0
 */
public class VideoFull {
    private VideoLight shared_info;
    private String file_path;
    private VideoFormat video_format;
    private Map<UUID, Rating> ratings;
    private Map<UUID, Comment> comments;

    public VideoFull() {
    }

    public VideoFull(VideoLight shared_info, String file_path, VideoFormat video_format, Map<UUID, Rating> ratings, Map<UUID, Comment> comments) {
        this.shared_info = shared_info;
        this.file_path = file_path;
        this.video_format = video_format;
        this.ratings = ratings;
        this.comments = comments;
    }

    public VideoFull(UserLight owner, String title, String creator, Set<String> tags, String file_path, VideoFormat video_format, Map<UUID, Rating> ratings, Map<UUID, Comment> comments) {
        this.shared_info = new VideoLight(owner, title, creator, tags);
        this.file_path = file_path;
        this.video_format = video_format;
        this.ratings = ratings;
        this.comments = comments;
    }

    public VideoFull(UserLight owner, String title, String creator, Set<String> tags, String file_path, VideoFormat video_format) {
        this.shared_info = new VideoLight(owner, title, creator, tags);
        this.file_path = file_path;
        this.video_format = video_format;
    }

    public VideoLight getSharedInfo() {
        return shared_info;
    }

    public void setSharedInfo(VideoLight shared_info) {
        this.shared_info = shared_info;
    }

    public String getFilePath() {
        return file_path;
    }

    public void setFilePath(String file_path) {
        this.file_path = file_path;
    }

    public VideoFormat getVideoFormat() {
        return video_format;
    }

    public void setVideoFormat(VideoFormat video_format) {
        this.video_format = video_format;
    }

    public Map<UUID, Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Map<UUID, Rating> ratings) {
        this.ratings = ratings;
    }

    public Map<UUID, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<UUID, Comment> comments) {
        this.comments = comments;
    }
}
