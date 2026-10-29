package client.ihmmain;

import common.dataclasses.UserLight;
import javafx.application.Platform;
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

    private boolean connection_pending;
    private String requested_ip;
    private int requested_port;

    /** « Se connecter » : Data authentifie en local puis Comm se connecte au serveur. */
    @FXML
    public void handleConnexion() {
        if (connection_pending) {
            return;
        }
        String error = validate(name_field.getText(), pwd_field.getText(), ip_field.getText(), port_field.getText());
        displayError(error);
        if (error != null || !isAvailable(data_calls, "Data")) {
            return;
        }
        String ip = ip_field.getText().strip();
        int port = Integer.parseInt(port_field.getText().strip());
        // L'identité authentifiée appartient à Data : ne pas créer de profil ici.
        requested_ip = ip;
        requested_port = port;
        connection_pending = true;
        try {
            data_calls.requestConnection(name_field.getText().strip(), pwd_field.getText(), ip, port);
        } catch (RuntimeException e) {
            connection_pending = false;
            displayError("Connexion impossible : " + e.getMessage());
            return;
        }
        if (connection_pending) {
            displayError("Demande envoyée. En attente de confirmation de connexion.");
        }
    }

    /** « Créer un profil » : ouvre l'écran 02. */
    @FXML
    public void handleCreateProfile() {
        navigate(name_field, CreateProfileController.VIEW);
    }

    /** Appelé via DataCallsIHMMain#raiseException quand l'authentification échoue. */
    // TODO: utiliser AuthenticationFailed lorsque ce type référencé par InterfaceDataCallsIHMMain sera défini
    public void raiseException(Exception authentication_failed) {
        onFxThread(() -> {
            connection_pending = false;
            String message = authentication_failed == null ? null : authentication_failed.getMessage();
            displayError(message == null || message.isBlank() ? "Pseudo ou mot de passe incorrect." : message);
        });
    }

    /**
     * Point d'affichage à raccorder lors de l'intégration des modules, après confirmation effective.
     * Le profil doit être celui authentifié par Data, avec son identité existante.
     * Ce point d'entrée ne constitue pas une nouvelle interface inter-module.
     */
    public void displayConnectionSuccess(UserLight authenticated_profile) {
        onFxThread(() -> {
            if (!connection_pending) {
                return;
            }
            if (authenticated_profile == null) {
                raiseException(new IllegalArgumentException("Le profil authentifié est indisponible."));
                return;
            }
            connection_pending = false;
            setLocalUser(authenticated_profile);
            setServer(requested_ip, requested_port);
            displayError(null);
            navigate(name_field, HomeController.VIEW);
        });
    }

    private static void onFxThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
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
