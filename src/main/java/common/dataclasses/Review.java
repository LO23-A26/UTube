package common.dataclasses;

import java.util.Date;
import java.util.UUID;

public abstract class Review {
    private UserLight author;
    private UUID video_id;
    private Date date;

    public UserLight getAuthor() {
        return author;
    }

    public void setAuthor(UserLight author) {
        this.author = author;
    }

    public UUID getVideoId() {
        return video_id;
    }

    public void setVideoId(UUID video_id) {
        this.video_id = video_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
