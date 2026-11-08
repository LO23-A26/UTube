package common.exceptions;

/**
 * Exception raised when a user fails to authenticate (wrong pseudo or password, connection refused...).
 *
 * @author IHM-Main
 * @version 1.0
 */
public class AuthenticationFailed extends Exception {

    public AuthenticationFailed(String message) {
        super(message);
    }

    public AuthenticationFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
