package client.ihmmain;

import common.dataclasses.UserLight;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Contrôleur de l'écran de connexion (fig. 6.4, maquette 01, séquence 5.1.2.1).
 */
public class ConnexionController extends Controller {

    public static final String VIEW = "/client/ihmmain/connexion-view.fxml";

    @FXML
    public TextField name_field;
    @FXML
    public PasswordField pwd_field;
    @FXML
    public Label connexion_error;
    @FXML
    private TextField ip_field;
    @FXML
    private TextField port_field;

    /** Vrai si Data a signalé un échec d'authentification pendant requestConnection. */
    private boolean authentication_failed;

    /** « Se connecter » : Data authentifie en local puis Comm se connecte au serveur. */
    @FXML
    public void handleConnexion() {
        String error = validate(name_field.getText(), pwd_field.getText(), ip_field.getText(), port_field.getText());
        displayError(error);
        if (error != null || !isAvailable(data_calls, "Data")) {
            return;
        }
        String ip = ip_field.getText().strip();
        int port = Integer.parseInt(port_field.getText().strip());
        UserLight user_profile = new UserLight(name_field.getText().strip());

        authentication_failed = false;
        try {
            data_calls.requestConnection(user_profile.getPseudo(), pwd_field.getText(), ip, port);
        } catch (RuntimeException e) {
            displayError("Connexion impossible : " + e.getMessage());
            return;
        }
        if (authentication_failed) {
            return;
        }
        setLocalUser(user_profile);
        setServer(ip, port);
        navigate(name_field, HomeController.VIEW);
    }

    /** « Créer un profil » : ouvre l'écran 02. */
    @FXML
    public void handleCreateProfile() {
        navigate(name_field, CreateProfileController.VIEW);
    }

    /** Appelé via DataCallsIHMMain#raiseException quand l'authentification échoue. */
    // TODO: utiliser AuthenticationFailed lorsque ce type référencé par InterfaceDataCallsIHMMain sera défini
    public void raiseException(Exception authentication_failed) {
        this.authentication_failed = true;
        String message = authentication_failed.getMessage();
        displayError(message == null || message.isBlank() ? "Pseudo ou mot de passe incorrect." : message);
    }

    /** « Importer un profil » : sélecteur de fichier système puis import local par Data (séquence 5.1.4.2). */
    @FXML
    private void handleImportProfile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importer un profil");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archive de profil", "*.zip"));
        File file = chooser.showOpenDialog(name_field.getScene().getWindow());
        if (file == null || !isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            data_calls.importProfileData(file.getAbsolutePath());
            displayError(null);
        } catch (RuntimeException e) {
            displayError("Import impossible : " + e.getMessage());
        }
    }

    private void displayError(String message) {
        connexion_error.setText(message);
        connexion_error.setVisible(message != null);
    }

    /** Renvoie le message d'erreur à afficher, ou null si la saisie est valide. */
    static String validate(String pseudo, String password, String ip, String port) {
        if (pseudo.isBlank() || password.isEmpty() || ip.isBlank() || port.isBlank()) {
            return "Tous les champs sont obligatoires.";
        }
        return validatePort(port);
    }

    /** Renvoie le message d'erreur du port, ou null s'il est valide. */
    static String validatePort(String port) {
        try {
            int p = Integer.parseInt(port.strip());
            if (p < 1 || p > 65535) {
                return "Le port doit être compris entre 1 et 65535.";
            }
        } catch (NumberFormatException e) {
            return "Le port doit être un nombre.";
        }
        return null;
    }
}
