package client.ihmmain;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

/**
 * Controller of the "Mon profil" screen (mockup 12): displays the local profile
 * and lets the user edit it. Opened by a click on the avatar of the header.
 */
public class ProfileController extends Controller {

    @FXML
    private Circle avatarPreview;

    @FXML
    private TextField pseudoField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private Label errorLabel;

    private String avatarPath;

    @Override
    public void refreshView() {
        errorLabel.setVisible(false);
        passwordField.clear();
        /*
         * TODO: pre-fill the form with the local profile once Data exposes it,
         * for example: UserFull profile = viewManager.getIData().getLocalProfile();
         * then call fillForm(...) with the fields of this profile.
         */
    }

    /**
     * Pre-fills the form with the current values of the profile.
     * Every parameter may be null when the information is not known.
     */
    void fillForm(String pseudo, String lastName, String firstName, LocalDate birthDate, String avatarPath) {
        pseudoField.setText(pseudo == null ? "" : pseudo);
        lastNameField.setText(lastName == null ? "" : lastName);
        firstNameField.setText(firstName == null ? "" : firstName);
        birthDatePicker.setValue(birthDate);
        if (avatarPath != null) {
            displayAvatar(new File(avatarPath));
        }
    }

    /**
     * Opens a file chooser to select a new avatar.
     */
    @FXML
    private void chooseAvatar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un avatar");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File selectedFile = fileChooser.showOpenDialog(
                pseudoField.getScene().getWindow()
        );

        if (selectedFile != null) {
            displayAvatar(selectedFile);
        }
    }

    private void displayAvatar(File avatarFile) {
        avatarPath = avatarFile.getAbsolutePath();
        Image avatar = new Image(avatarFile.toURI().toString());
        avatarPreview.setFill(new ImagePattern(avatar));
    }

    /**
     * Cancels the changes and returns to the home screen.
     */
    @FXML
    private void cancel() {
        viewManager.showContent(IHMMainManager.HOME_VIEW);
    }

    /**
     * Validates the form before saving the changes of the profile.
     */
    @FXML
    private void saveProfile() {
        String error = validate(
                pseudoField.getText(),
                birthDatePicker.getValue()
        );

        errorLabel.setText(error);
        errorLabel.setVisible(error != null);

        if (error == null) {
            /*
             * TODO: update the local profile through Data, for example with
             * viewManager.getIData().updateLocalProfile(...).
             * updateLocalProfile currently takes a UserLight, which carries neither the password
             * nor the last name, first name and birth date: the Data interface must first be
             * aligned with the UserFull model (same TODO as in CreateProfileController).
             * An empty password field means that the password is not changed.
             */
            viewManager.showContent(IHMMainManager.HOME_VIEW);
        }
    }

    /**
     * Returns the error message to display, or null if the input is valid.
     * The password is optional here: when it is left empty, it is not changed.
     */
    static String validate(String pseudo, LocalDate birthDate) {
        if (pseudo.isBlank()) {
            return "Le pseudo est obligatoire.";
        }

        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas être dans le futur.";
        }

        return null;
    }
}
