package common.dataclasses;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
 * Abstract class representing a user review on a video.
 *
 * @author Jean Van Gysel
 * @version 1.0
 */
public abstract class Review {
    private UserLight author;
    private UUID video_id;
    private LocalDate date;

    protected Review() {
    }

    protected Review(UserLight author, UUID video_id, LocalDate date) {
        this.author = author;
        this.video_id = video_id;
        this.date = date;
    }

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
