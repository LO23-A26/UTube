package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
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
 * given by Data and lets the user edit it. Opened by a click on the avatar of the header.
 */
public class ProfileController extends Controller {

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

    /** Local profile given by Data, edited then sent back to Data. Null if Data does not provide it. */
    private UserFull profile;

    private String avatar_path;

    /** Called by the FXMLLoader once the view is loaded: fills the fields with the local profile. */
    @FXML
    private void initialize() {
        InterfaceIhmMainCallsDataClient data = viewManager.getIData();
        profile = data == null ? null : data.getUser();
        if (profile == null) {
            displayError("Profil indisponible.");
            return;
        }

        UserLight shared_info = profile.getSharedInfo();
        pseudo_field.setText(shared_info == null || shared_info.getPseudo() == null ? "" : shared_info.getPseudo());
        last_name_field.setText(profile.getLastName() == null ? "" : profile.getLastName());
        first_name_field.setText(profile.getFirstName() == null ? "" : profile.getFirstName());
        birth_date_picker.setValue(profile.getBirthDate());

        avatar_path = shared_info == null ? null : shared_info.getAvatarPath();
        if (avatar_path != null && new File(avatar_path).isFile()) {
            displayAvatar(new File(avatar_path));
        }
    }

    /**
     * Opens a file chooser to select a new avatar.
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
            displayAvatar(selected_file);
        }
    }

    private void displayAvatar(File avatar_file) {
        Image avatar = new Image(avatar_file.toURI().toString());
        avatar_preview.setFill(new ImagePattern(avatar));
    }

    /**
     * Cancels the changes and returns to the home screen.
     */
    @FXML
    private void cancel() {
        viewManager.getController(MainController.class).backToHome();
    }

    /**
     * Validates the form, updates the local profile through Data and returns to the home screen.
     */
    @FXML
    private void saveProfile() {
        String error = validate(
                pseudo_field.getText(),
                birth_date_picker.getValue()
        );

        if (error == null && profile == null) {
            error = "Profil indisponible.";
        }

        if (error != null) {
            displayError(error);
            return;
        }

        UserLight shared_info = profile.getSharedInfo();
        shared_info.setPseudo(pseudo_field.getText().strip());
        if (avatar_path != null && !avatar_path.equals(shared_info.getAvatarPath())) {
            shared_info.setAvatarPath(avatar_path);
            shared_info.setAvatar(avatar_path);
        }
        profile.setLastName(last_name_field.getText().strip());
        profile.setFirstName(first_name_field.getText().strip());
        profile.setBirthDate(birth_date_picker.getValue());
        // An empty password field means that the password is not changed
        if (!password_field.getText().isEmpty()) {
            profile.setPassword(password_field.getText());
        }

        try {
            viewManager.getIData().updateLocalProfile(profile);
        } catch (RuntimeException e) {
            displayError("Impossible d'enregistrer le profil.");
            return;
        }

        viewManager.getController(MainController.class).backToHome();
    }

    private void displayError(String message) {
        error_label.setText(message);
        error_label.setVisible(true);
    }

    /**
     * Returns the error message to display, or null if the input is valid.
     * The password is optional here: when it is left empty, it is not changed.
     */
    static String validate(String pseudo, LocalDate birth_date) {
        if (pseudo == null || pseudo.isBlank()) {
            return "Le pseudo est obligatoire.";
        }

        if (birth_date != null && birth_date.isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas être dans le futur.";
        }

        return null;
    }
}
