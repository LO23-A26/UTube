package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the formatting of the user's identity, the presence of the view FXML on the classpath and
 * the detection of the awaited profile.
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

    /** Checks that the user detail view is available on the classpath. */
    @Test
    void userDetailViewIsOnClasspath() {
        assertNotNull(IHMMainManager.class.getResource(IHMMainManager.USER_DETAIL_VIEW));
    }

    /** Checks that the expected user's profile is accepted. */
    @Test
    void isAwaitedProfileWithExpectedUser() {
        UserFull profile = createUser("alice");
        UserLight awaited = profile.getSharedInfo();
        assertTrue(UserDetailController.isAwaitedProfile(awaited, profile));
    }

    /** Checks that another user's profile is rejected. */
    @Test
    void isAwaitedProfileWithOtherUser() {
        UserLight awaited = createUser("alice").getSharedInfo();
        UserFull profile = createUser("bastien");
        assertFalse(UserDetailController.isAwaitedProfile(awaited, profile));
    }

    /** Checks that an unavailable profile is accepted while waiting. */
    @Test
    void isAwaitedProfileWithNullWhileWaiting() {
        UserLight awaited = createUser("alice").getSharedInfo();
        assertTrue(UserDetailController.isAwaitedProfile(awaited, null));
    }

    /** Checks that a profile is rejected when no user is expected. */
    @Test
    void isAwaitedProfileWhenNothingIsAwaited() {
        UserFull profile = createUser("alice");
        assertFalse(UserDetailController.isAwaitedProfile(null, profile));
    }

    /**
     * Creates a user for profile matching tests.
     *
     * @param pseudo the user's pseudo
     * @return the test user
     */
    private static UserFull createUser(String pseudo) {
        return new UserFull(pseudo, "secret", "Alice", "Dupont", LocalDate.of(2005, 3, 15));
    }
}
