package client.ihmmain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProfileControllerTest {

    @Test
    void validate() {
        assertNull(ProfileController.validate("alice", null));
        assertNull(ProfileController.validate("alice", LocalDate.of(2003, 5, 12)));
        assertNotNull(ProfileController.validate("", null));
        assertNotNull(ProfileController.validate("   ", null));
        assertNotNull(ProfileController.validate("alice", LocalDate.now().plusDays(1)));
    }
}
