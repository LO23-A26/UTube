package common.interfaces.comm;

import java.util.UUID;

import common.dataclasses.Comment;
import common.dataclasses.Rating;

public interface InterfaceIHMShareCallsComm {
    void addComment(Comment comment, UUID owner);

    void editComment(Comment comment, UUID owner);

    void deleteComment(Comment comment, UUID owner);

    void addRating(Rating rating, UUID owner);

    void editRating(Rating rating, UUID owner);

    void deleteRating(Rating rating, UUID owner);

    void askVideo(UUID videoId, UUID ownerId);
}
