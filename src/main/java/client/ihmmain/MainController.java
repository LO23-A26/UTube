package client.ihmmain;

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
}
