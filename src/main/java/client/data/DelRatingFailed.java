package client.data;

public class DelRatingFailed extends RuntimeException {
    public DelRatingFailed(String message) {
        super(message);
    }
    public DelRatingFailed(){
        super("Delete rating failed");
    }
}
