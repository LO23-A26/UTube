package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.Comment;
import common.dataclasses.communication.ClientMessage;

public class EditCommentMessage implements  ClientMessage {
    Comment comment;

    public EditCommentMessage(Comment comment) {
        this.comment = comment;
    }

    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.editComment(comment);
    }

    
}
