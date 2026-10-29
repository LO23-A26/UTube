package client.ihmmain;

import common.dataclasses.UserLight;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Contrôleur de la modification et de l'export du profil local (fig. 6.4, maquette 12,
 * séquences 5.1.1.2 et 5.1.4).
 */
public class ProfileController extends Controller {

    public static final String VIEW = "/client/ihmmain/profile-view.fxml";

    // ponytail: Data n'expose pas le profil complet (pas de getLocalProfile), seul le profil léger est connu
    public UserLight current_profile;

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
    private Label feedback_label;

    private String avatar_path;

    @Override
    public void refreshView() {
        loadProfile();
    }

    /** Pré-remplit le formulaire avec le profil courant. */
    public void loadProfile() {
        current_profile = local_user;
        if (current_profile == null) {
            return;
        }
        pseudo_field.setText(current_profile.getPseudo());
        avatar_path = current_profile.getImage_path();
        fillAvatar(avatar_circle, avatar_path);
    }

    /** « Enregistrer les modifications ». */
    @FXML
    public void editProfile() {
        if (pseudo_field.getText().isBlank()) {
            displayFeedback("Le pseudo est obligatoire.");
            return;
        }
        submitProfileForm(pseudo_field.getText().strip(), pwd_field.getText(), avatar_path);
    }

    /** Data met à jour le fichier de profil existant, puis retour à l'Accueil (séquence 5.1.1.2). */
    public void submitProfileForm(String pseudo, String password, String avatar_path) {
        if (current_profile == null || !isAvailable(data_calls, "Data")) {
            return;
        }
        current_profile.setPseudo(pseudo);
        if (avatar_path != null) {
            current_profile.setImage_path(avatar_path);
            current_profile.setAvatar(avatar_path);
        }
        // ponytail: updateLocalProfile(UserLight) ne transporte ni mot de passe ni nom/prénom/date, à élargir côté Data
        try {
            data_calls.updateLocalProfile(current_profile);
        } catch (RuntimeException e) {
            displayFeedback("Enregistrement impossible : " + e.getMessage());
            return;
        }
        showHomeScreen();
    }

    public void showHomeScreen() {
        home.showHomeScreen();
        home.refreshView();
    }

    /** « Exporter le profil » : archive locale du profil, des vidéos partagées et de leurs avis (séquence 5.1.4.1). */
    @FXML
    private void handleExportProfile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter le profil");
        chooser.setInitialFileName((current_profile == null ? "profil" : current_profile.getPseudo()) + ".zip");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archive de profil", "*.zip"));
        File file = chooser.showSaveDialog(pseudo_field.getScene().getWindow());
        if (file != null) {
            exportProfile(file.getAbsolutePath());
        }
    }

    public void exportProfile(String path) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            data_calls.exportProfileData(path);
            displayFeedback("Profil exporté dans " + path);
        } catch (RuntimeException e) {
            displayFeedback("Export impossible : " + e.getMessage());
        }
    }

    /** Import local d'un profil exporté (séquence 5.1.4.2) ; l'écran 01 propose aussi « Importer un profil ». */
    public void importProfile(String path) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            data_calls.importProfileData(path);
            loadProfile();
        } catch (RuntimeException e) {
            displayFeedback("Import impossible : " + e.getMessage());
        }
    }

    @FXML
    private void changeAvatar() {
        File file = CreateProfileController.chooseImage(avatar_circle);
        if (file != null) {
            avatar_path = file.getAbsolutePath();
            fillAvatar(avatar_circle, avatar_path);
        }
    }

    private void displayFeedback(String message) {
        feedback_label.setText(message);
        feedback_label.setVisible(true);
    }
}
