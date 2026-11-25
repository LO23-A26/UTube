package client.ihmmain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateProfileControllerTest {

    @Test
    void validate() {
        assertNull(CreateProfileController.validate("alice", "secret"));
        assertNotNull(CreateProfileController.validate("", "secret"));
        assertNotNull(CreateProfileController.validate("   ", "secret"));
        assertNotNull(CreateProfileController.validate("alice", ""));
    }
}