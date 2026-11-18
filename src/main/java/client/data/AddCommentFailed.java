package client.data;

public class AddCommentFailed extends RuntimeException {
    public AddCommentFailed(String message) {
        super(message);
    }
    public AddCommentFailed(){
        super("Add comment failed");
    }
}