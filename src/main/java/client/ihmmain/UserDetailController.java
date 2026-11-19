package client.ihmmain;

import common.dataclasses.UserFull;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.Period;

/**
 * Displays the selected user's profile in the content area of the main view.
 *
 * @author Santiago Avellaneda Rodriguez
 * @version 1.0
 */
public class UserDetailController extends Controller {

    private static final int INITIALS_LENGTH = 2;

    @FXML
    private Label avatar_label;
    @FXML
    private Label pseudo_label;
    @FXML
    private Label identity_label;

    private UserFull profile;

    /**
     * Stores the selected profile and updates its displayed information.
     *
     * @param profile the user profile to display
     */
    public void displayProfile(UserFull profile) {
        this.profile = profile;
        refreshView();
    }

    /** Updates the profile header once a profile has been provided. */
    @Override
    public void refreshView() {
        if (profile == null) {
            return;
        }
        String pseudo = profile.getSharedInfo().getPseudo();
        pseudo_label.setText(pseudo);
        avatar_label.setText(pseudo.substring(0, Math.min(INITIALS_LENGTH, pseudo.length())).toUpperCase());
        identity_label.setText(formatIdentity(profile.getFirstName(), profile.getLastName(),
                profile.getBirthDate(), LocalDate.now()));
    }

    /**
     * Formats the user's full name and age when a birth date is available.
     *
     * @param first_name the user's first name
     * @param last_name the user's last name
     * @param birth_date the user's birth date, or null if unknown
     * @param today the date used to calculate the age
     * @return the last name followed by the first name and the age if known
     */
    static String formatIdentity(String first_name, String last_name, LocalDate birth_date, LocalDate today) {
        String full_name = last_name + " " + first_name;
        if (birth_date == null) {
            return full_name;
        }
        int age = Period.between(birth_date, today).getYears();
        return full_name + " · " + age + " ans";
    }

    /** Returns to the home screen in the content area of the main view. */
    @FXML
    private void goBack() {
        viewManager.showContent(IHMMainManager.HOME_VIEW);
    }
}
