package client.ihmmain;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.scene.control.DatePicker;
import common.dataclasses.UserFull;

import java.io.File;

/**
 * Controller of the profile creation screen.
 */
public class CreateProfileController extends Controller {

    @FXML
    private Circle avatar_preview;

    @FXML
    private TextField pseudo_field;

    @FXML
    private PasswordField password_field;

    @FXML
    private TextField last_name_field;

    @FXML
    private TextField first_name_field;

    @FXML
    private DatePicker birth_date_picker;

    @FXML
    private Label error_label;

    private String avatar_path;

    @Override
    public void refreshView() {
        error_label.setVisible(false);
    }

    /**
     * Opens a file chooser to select the user's avatar.
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
            avatarPath = selectedFile.getAbsolutePath();

            Image avatar = new Image(selectedFile.toURI().toString());
            avatarPreview.setFill(new ImagePattern(avatar));
        }
    }

    /**
     * Cancels profile creation and returns to the login screen.
     */
    @FXML
    private void cancel() {
        viewManager.changeView(IHMMainManager.LOGIN_VIEW);
    }

    /**
     * Validates the form before creating the profile.
     */
    @FXML
    private void createProfile() {
        String error = validate(
                pseudoField.getText(),
                passwordField.getText()
        );

        errorLabel.setText(error);
        errorLabel.setVisible(error != null);

        if (error == null) {

            UserFull profile;

            if (avatarPath != null) {
                profile = new UserFull(
                        pseudoField.getText().strip(),
                        avatarPath,
                        passwordField.getText(),
                        firstNameField.getText().strip(),
                        lastNameField.getText().strip(),
                        birthDatePicker.getValue()
                );
            } else {
                profile = new UserFull(
                        pseudoField.getText().strip(),
                        passwordField.getText(),
                        firstNameField.getText().strip(),
                        lastNameField.getText().strip(),
                        birthDatePicker.getValue()
                );
            }

            viewManager.getIData().saveLocalProfile(profile);

            viewManager.changeView(IHMMainManager.LOGIN_VIEW);
        }
    }

    /**
     * Returns the error message to display, or null if the input is valid.
     */
    static String validate(String pseudo, String password) {
        if (pseudo.isBlank() || password.isEmpty()) {
            return "Le pseudo et le mot de passe sont obligatoires.";
        }

        return null;
    }
}