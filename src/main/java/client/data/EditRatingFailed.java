package client.data;

public class EditRatingFailed extends RuntimeException {
    public EditRatingFailed(String message) {
        super(message);
    }
    public EditRatingFailed(){
        super("Rating comment failed");
    }
}
