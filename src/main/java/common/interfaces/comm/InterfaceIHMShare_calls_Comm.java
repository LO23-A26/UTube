package common.interfaces.comm;

import java.util.UUID;

import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.UserLight;

public interface InterfaceIHMShare_calls_Comm {
    void addComment(Comment comment, UserLight owner);

    void editComment(Comment comment, UserLight owner);

    void deleteComment(Comment comment, UserLight owner);

    void addRating(Rating rating, UserLight owner);

    void editRating(Rating rating, UserLight owner);

    void deleteRating(Rating rating, UserLight owner);

    void askVideo(UUID videoId, UUID ownerId);
}
