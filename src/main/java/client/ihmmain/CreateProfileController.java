package client.ihmmain;

import common.dataclasses.UserLight;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

/**
 * Contrôleur de la création d'un profil local (maquette 02, séquence 5.1.1.1).
 */
public class CreateProfileController extends Controller {

    public static final String VIEW = "/client/ihmmain/create-profile-view.fxml";

    @FXML
    private Circle avatar_circle;
    @FXML
    private TextField pseudo_field;
    @FXML
    private PasswordField pwd_field;
    @FXML
    private TextField last_name_field;
    @FXML
    private TextField first_name_field;
    @FXML
    private DatePicker birth_date_picker;
    @FXML
    private Label error_label;

    private String avatar_path;

    @FXML
    private void chooseAvatar() {
        File file = chooseImage(avatar_circle);
        if (file != null) {
            avatar_path = file.getAbsolutePath();
            fillAvatar(avatar_circle, avatar_path);
        }
    }

    /** « Créer le profil ». */
    @FXML
    private void createProfile() {
        String error = validate(pseudo_field.getText(), pwd_field.getText(), birth_date_picker.getValue());
        displayError(error);
        if (error == null) {
            submitProfileForm(pseudo_field.getText().strip(), pwd_field.getText(), avatar_path);
        }
    }

    /** Data crée le fichier de profil sur la machine, puis retour à l'écran de connexion. */
    public void submitProfileForm(String pseudo, String password, String avatar_path) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        UserLight profile = avatar_path == null ? new UserLight(pseudo) : new UserLight(pseudo, avatar_path);
        // ponytail: saveLocalProfile(UserLight) ne transporte ni mot de passe ni nom/prénom/date, à élargir côté Data (UserFull)
        try {
            data_calls.saveLocalProfile(profile);
        } catch (RuntimeException e) {
            displayError("Création impossible : " + e.getMessage());
            return;
        }
        showLoginScreen();
    }

    /** « Annuler » et fin de création : retour à 01. */
    @FXML
    public void showLoginScreen() {
        navigate(pseudo_field, ConnexionController.VIEW);
    }

    private void displayError(String message) {
        error_label.setText(message);
        error_label.setVisible(message != null);
    }

    /** Renvoie le message d'erreur à afficher, ou null si la saisie est valide. */
    static String validate(String pseudo, String password, LocalDate birth_date) {
        if (pseudo.isBlank() || password.isEmpty()) {
            return "Le pseudo et le mot de passe sont obligatoires.";
        }
        if (birth_date != null && birth_date.isAfter(LocalDate.now())) {
            return "La date de naissance ne peut pas être dans le futur.";
        }
        return null;
    }

    /** Sélecteur d'image pour l'avatar (création et modification du profil). */
    static File chooseImage(Node anchor) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un avatar");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        return chooser.showOpenDialog(anchor.getScene().getWindow());
    }
}
