package common.exceptions;

/**
 * Thrown when a user is not allowed to access a video.
 *
 * @author Florian Goutard
 * @version 1.0
 */
public class AccessDeniedException extends Exception {

    /**
     * @param message explanation of the refusal
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}