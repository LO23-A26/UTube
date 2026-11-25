package common.dataclasses;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Rating value left by a user on a video.
 *
 * @author Jean Van Gysel, Lucas Dompnier
 * @version 2.0
 */
public class Rating extends Review implements Serializable {
    private int value;

    public Rating() {
        super();
    }

    public Rating(UserLight author, UUID video_id, LocalDate date, int value) {
        super(author, video_id, date);
        this.value = value;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "value=" + value +
                '}';
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
