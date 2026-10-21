package common.dataclasses;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

/**
 * Rating value left by a user on a video.
 *
 * @author Jean Van Gysel
 * @version 1.0
 */
public class Rating extends Review {
    private int value;

    public Rating() {
        super();
    }

    public Rating(UserLight author, UUID video_id, LocalDate date, int value) {
        super(author, video_id, date);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
