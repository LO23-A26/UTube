package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.Rating;
import common.dataclasses.communication.ClientMessage;

public class EditRatingMessage implements ClientMessage {
    Rating rating;

    public EditRatingMessage(Rating rating) {
        this.rating = rating;
    }

    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.addRating(rating);
    } 
}
