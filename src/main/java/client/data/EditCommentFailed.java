package client.data;

public class EditCommentFailed extends RuntimeException {
    public EditCommentFailed(String message) {
        super(message);
    }
    public EditCommentFailed(){
        super("Edit comment failed");
    }
}
