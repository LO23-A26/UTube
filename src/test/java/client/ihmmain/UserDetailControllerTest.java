package client.ihmmain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Checks the formatting of the user's full name and age in the profile header.
 *
 * @author Santiago Avellaneda Rodriguez
 * @version 1.0
 */
class UserDetailControllerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 19);

    /** Checks the age when the user's birthday has already passed this year. */
    @Test
    void formatIdentityAfterBirthday() {
        assertEquals("Dupont Alice · 21 ans", UserDetailController.formatIdentity(
                "Alice", "Dupont", LocalDate.of(2005, 3, 15), TODAY));
    }

    /** Checks that the age is one year lower before the user's birthday. */
    @Test
    void formatIdentityBeforeBirthday() {
        assertEquals("Dupont Alice · 20 ans", UserDetailController.formatIdentity(
                "Alice", "Dupont", LocalDate.of(2005, 12, 15), TODAY));
    }

    /** Checks that only the full name is displayed when the birth date is unknown. */
    @Test
    void formatIdentityWithoutBirthDate() {
        assertEquals("Dupont Alice", UserDetailController.formatIdentity(
                "Alice", "Dupont", null, TODAY));
    }
}
