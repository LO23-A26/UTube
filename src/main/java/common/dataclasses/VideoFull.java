package common.dataclasses;

import java.nio.file.Path;
import java.util.Map;

public class VideoFull {
    private VideoLight shared_info;
    private Path file_path;
    private VideoFormat video_format;
    private Map<String, Rating> ratings;
    private Map<String, Comment> comments;

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
