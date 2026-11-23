package client.ihmmain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateProfileControllerTest {

    @Test
    void validate() {
        assertNull(CreateProfileController.validate("alice", "secret", null));
        assertNotNull(CreateProfileController.validate("", "secret", null));
        assertNotNull(CreateProfileController.validate("   ", "secret", null));
        assertNotNull(CreateProfileController.validate("alice", "", null));
        assertNotNull(CreateProfileController.validate("alice", "secret", LocalDate.now().plusDays(1)));
        assertNull(CreateProfileController.validate("alice", "secret", LocalDate.of(2004, 5, 10)));
    }
}