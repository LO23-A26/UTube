package common.dataclasses.communication.messages;

import client.communication.CommClientController;
import common.dataclasses.Comment;
import common.dataclasses.communication.ClientMessage;

public class DeleteCommentMessage implements ClientMessage {
   Comment comment;

   public DeleteCommentMessage(Comment comment) {
    this.comment = comment;
   } 

    @Override
    public void handle(CommClientController controller) {
        controller.interfaceCommDataClient.deleteComment(comment.getVideoId(), comment.getAuthor().getUuid());
    }
}
