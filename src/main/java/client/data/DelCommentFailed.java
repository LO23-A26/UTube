package client.data;

public class DelCommentFailed extends RuntimeException {
    public DelCommentFailed(String message) {
        super(message);
    }
    public DelCommentFailed(){
        super("Delete comment failed");
    }
}
