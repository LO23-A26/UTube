package common.dataclasses;

import java.nio.file.Path;
import java.util.Map;

/**
 * Full video data including file path, format, ratings and comments.
 *
 * @author Jean Van Gysel
 * @version 1.0
 */
public class VideoFull {
    private VideoLight shared_info;
    private Path file_path;
    private VideoFormat video_format;
    private Map<String, Rating> ratings;
    private Map<String, Comment> comments;

    public VideoFull() {
    }

    public VideoFull(VideoLight shared_info, Path file_path, VideoFormat video_format, Map<String, Rating> ratings, Map<String, Comment> comments) {
        this.shared_info = shared_info;
        this.file_path = file_path;
        this.video_format = video_format;
        this.ratings = ratings;
        this.comments = comments;
    }

    public VideoLight getSharedInfo() {
        return shared_info;
    }

    public void setSharedInfo(VideoLight shared_info) {
        this.shared_info = shared_info;
    }

    public Path getFilePath() {
        return file_path;
    }

    public void setFilePath(Path file_path) {
        this.file_path = file_path;
    }

    public VideoFormat getVideoFormat() {
        return video_format;
    }

    public void setVideoFormat(VideoFormat video_format) {
        this.video_format = video_format;
    }

    public Map<String, Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Map<String, Rating> ratings) {
        this.ratings = ratings;
    }

    public Map<String, Comment> getComments() {
        return comments;
    }

    public void setComments(Map<String, Comment> comments) {
        this.comments = comments;
    }
}
