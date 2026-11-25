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
import java.time.LocalDate;

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
        FileChooser file_chooser = new FileChooser();
        file_chooser.setTitle("Choisir un avatar");

        file_chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images",
                        "*.png",
                        "*.jpg",
                        "*.jpeg"
                )
        );

        File selected_file = file_chooser.showOpenDialog(
                pseudo_field.getScene().getWindow()
        );

        if (selected_file != null) {
            avatar_path = selected_file.getAbsolutePath();

            Image avatar = new Image(selected_file.toURI().toString());
            avatar_preview.setFill(new ImagePattern(avatar));
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
                pseudo_field.getText(),
                password_field.getText(),
                birth_date_picker.getValue()
        );

        error_label.setText(error);
        error_label.setVisible(error != null);

        if (error == null) {

            if (viewManager.getIData() == null) {
                error_label.setText("Le service de création de profil est indisponible.");
                error_label.setVisible(true);
                return;
            }

            UserFull profile;

            if (avatar_path != null) {
                profile = new UserFull(
                        pseudo_field.getText().strip(),
                        avatar_path,
                        password_field.getText(),
                        first_name_field.getText().strip(),
                        last_name_field.getText().strip(),
                        birth_date_picker.getValue()
                );
            } else {
                profile = new UserFull(
                        pseudo_field.getText().strip(),
                        password_field.getText(),
                        first_name_field.getText().strip(),
                        last_name_field.getText().strip(),
                        birth_date_picker.getValue()
                );
            }

            try {
                viewManager.getIData().saveLocalProfile(profile);
            } catch (RuntimeException e) {
                // Data could not save the profile (avatar copy or profile file): stay on the form
                error_label.setText("Impossible d'enregistrer le profil.");
                error_label.setVisible(true);
                return;
            }

            viewManager.changeView(IHMMainManager.LOGIN_VIEW);
        }
    }

    /**
     * Returns the error message to display, or null if the input is valid.
     */
    static String validate(String pseudo, String password, LocalDate birth_date) {
        if (pseudo.isBlank() || password.isEmpty()) {
            return "Le pseudo et le mot de passe sont obligatoires.";
        }

        if (birth_date != null && birth_date.isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas être dans le futur.";
        }

        return null;
    }
}
