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

    /** Local profile given by Data, edited then sent back to Data. Null if Data does not provide it. */
    private UserFull profile;

    private String avatarPath;

    @Override
    public void refreshView() {
        errorLabel.setVisible(false);
        passwordField.clear();

        InterfaceIhmMainCallsDataClient data = viewManager.getIData();
        profile = data == null ? null : data.getUser();
        if (profile == null) {
            displayError("Profil indisponible.");
            return;
        }

        UserLight sharedInfo = profile.getSharedInfo();
        pseudoField.setText(sharedInfo == null || sharedInfo.getPseudo() == null ? "" : sharedInfo.getPseudo());
        lastNameField.setText(profile.getLastName() == null ? "" : profile.getLastName());
        firstNameField.setText(profile.getFirstName() == null ? "" : profile.getFirstName());
        birthDatePicker.setValue(profile.getBirthDate());

        avatarPath = sharedInfo == null ? null : sharedInfo.getAvatarPath();
        if (avatarPath != null && new File(avatarPath).isFile()) {
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
            avatarPath = selectedFile.getAbsolutePath();
            displayAvatar(selectedFile);
        }
    }

    private void displayAvatar(File avatarFile) {
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
     * Validates the form, updates the local profile through Data and returns to the home screen.
     */
    @FXML
    private void saveProfile() {
        String error = validate(
                pseudoField.getText(),
                birthDatePicker.getValue()
        );

        if (error == null && profile == null) {
            error = "Profil indisponible.";
        }

        if (error != null) {
            displayError(error);
            return;
        }

        UserLight sharedInfo = profile.getSharedInfo();
        sharedInfo.setPseudo(pseudoField.getText().strip());
        if (avatarPath != null && !avatarPath.equals(sharedInfo.getAvatarPath())) {
            sharedInfo.setAvatarPath(avatarPath);
            sharedInfo.setAvatar(avatarPath);
        }
        profile.setLastName(lastNameField.getText().strip());
        profile.setFirstName(firstNameField.getText().strip());
        profile.setBirthDate(birthDatePicker.getValue());
        // An empty password field means that the password is not changed
        if (!passwordField.getText().isEmpty()) {
            profile.setPassword(passwordField.getText());
        }

        try {
            viewManager.getIData().updateLocalProfile(profile);
        } catch (RuntimeException e) {
            displayError("Enregistrement impossible : " + e.getMessage());
            return;
        }

        viewManager.showContent(IHMMainManager.HOME_VIEW);
    }

    private void displayError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Returns the error message to display, or null if the input is valid.
     * The password is optional here: when it is left empty, it is not changed.
     */
    static String validate(String pseudo, LocalDate birthDate) {
        if (pseudo == null || pseudo.isBlank()) {
            return "Le pseudo est obligatoire.";
        }

        if (birthDate != null && birthDate.isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas être dans le futur.";
        }

        return null;
    }
}
