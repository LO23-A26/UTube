package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.Comment;
import common.dataclasses.communication.ClientMessage;

public class AddCommentMessage implements ClientMessage{
    Comment commentaire;

    public AddCommentMessage(Comment commentaire) {
        this.commentaire = commentaire;
    }

    @Override 
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.addComment(commentaire);
    }
}
