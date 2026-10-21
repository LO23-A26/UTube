package client.ihmmain;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Fenêtre de confirmation avant une suppression (maquette « Fenêtres de confirmation »).
 * L'action n'est effectuée qu'après « Supprimer » / « Retirer ».
 */
public class ConfirmationDialogController extends Controller {

    public static final String VIEW = "/client/ihmmain/confirmation-dialog.fxml";

    @FXML
    private Label title_label;
    @FXML
    private Label message_label;
    @FXML
    private Button confirm_button;

    private boolean confirmed;

    /** Affiche la question et attend la réponse ; renvoie vrai si l'action est confirmée. */
    public boolean ask(String title, String message, String action) {
        title_label.setText(title);
        message_label.setText(message);
        confirm_button.setText(action);
        showDialog();
        return confirmed;
    }

    @FXML
    private void confirm() {
        confirmed = true;
        closeDialog();
    }

    @FXML
    private void cancel() {
        closeDialog();
    }
}
