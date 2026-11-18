package client.data;

public class AddRatingFailed extends RuntimeException {
    public AddRatingFailed(String message) {
        super(message);
    }
    public AddRatingFailed(){
        super("Add rating failed");
    }
}
