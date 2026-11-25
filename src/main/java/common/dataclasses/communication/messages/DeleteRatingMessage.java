package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.Rating;
import common.dataclasses.communication.ClientMessage;

public class DeleteRatingMessage implements ClientMessage {
    Rating rating;

    public DeleteRatingMessage(Rating raiting) {
        this.rating = raiting;
    }

    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.deleteRating(rating.getVideoId(), rating.getAuthor().getUuid());
    }
}
