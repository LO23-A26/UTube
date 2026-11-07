package common.dataclasses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Comment left by a user on a video.
 *
 * @author Jean Van Gysel, Lucas Dompnier
 * @version 2.0
 */
public class Comment extends Review implements Serializable {
    private String text;

    public Comment() {
        super();
    }

    public Comment(UserLight author, UUID video_id, LocalDate date, String text) {
        super(author, video_id, date);
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "text='" + text + '\'' +
                '}';
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
