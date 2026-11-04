package client.ihmmain;

import client.main.UTubeApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Contrôleur de l'écran de connexion (maquette 01).
 */
public class LoginController {

    @FXML
    private TextField pseudoField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField portField;
    @FXML
    private Label errorLabel;

    @FXML
    private void connect() {
        String error = validate(pseudoField.getText(), passwordField.getText(),
                ipField.getText(), portField.getText());
        errorLabel.setText(error);
        errorLabel.setVisible(error != null);
        if (error != null) {
            return;
        }
        // ponytail: aucune authentification ni connexion serveur, brancher les interfaces data/comm ici
        UTubeApplication.show(pseudoField.getScene(), UTubeApplication.MAIN_VIEW);
    }

    /** Renvoie le message d'erreur à afficher, ou null si la saisie est valide. */
    static String validate(String pseudo, String password, String ip, String port) {
        if (pseudo.isBlank() || password.isEmpty() || ip.isBlank() || port.isBlank()) {
            return "Tous les champs sont obligatoires.";
        }
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
