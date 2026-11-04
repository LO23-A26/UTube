package client.ihmmain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class LoginControllerTest {

    @Test
    void validate() {
        assertNull(LoginController.validate("alice", "secret", "127.0.0.1", "8080"));
        assertNotNull(LoginController.validate("", "secret", "127.0.0.1", "8080"));
        assertNotNull(LoginController.validate("alice", "", "127.0.0.1", "8080"));
        assertNotNull(LoginController.validate("alice", "secret", " ", "8080"));
        assertNotNull(LoginController.validate("alice", "secret", "127.0.0.1", "abc"));
        assertNotNull(LoginController.validate("alice", "secret", "127.0.0.1", "0"));
        assertNotNull(LoginController.validate("alice", "secret", "127.0.0.1", "70000"));
    }
}
