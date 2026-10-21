package client.ihmmain;

import client.main.UTubeApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Contrôleur de la vue principale du client.
 */
public class MainController {

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        statusLabel.setText("Bienvenue sur UTube");
    }

    /** "Se déconnecter" du header : retour à l'écran de connexion (maquette 01). */
    @FXML
    private void logout() {
        // ponytail: pas de déconnexion serveur, brancher l'interface comm ici
        UTubeApplication.show(statusLabel.getScene(), UTubeApplication.LOGIN_VIEW);
    }
}
