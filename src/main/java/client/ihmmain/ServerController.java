package client.ihmmain;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Contrôleur de la gestion du serveur (maquette 03) : un seul serveur à la fois.
 */
public class ServerController extends Controller {

    public static final String VIEW = "/client/ihmmain/server-view.fxml";

    @FXML
    private Label current_server_label;
    @FXML
    private TextField server_ip_field;
    @FXML
    private TextField server_port_field;
    @FXML
    private Label error_label;

    @Override
    public void refreshView() {
        current_server_label.setText(server_ip == null ? "Aucun serveur" : server_ip + " : " + server_port);
    }

    /** « Se déconnecter du serveur » : ferme la session et ramène à l'écran de connexion. */
    @FXML
    private void disconnectServer() {
        if (!isAvailable(comm_calls, "Communication")) {
            return;
        }
        try {
            comm_calls.closeSession();
        } catch (RuntimeException e) {
            displayError("Déconnexion impossible : " + e.getMessage());
            return;
        }
        setLocalUser(null);
        setServer(null, 0);
        navigate(server_ip_field, ConnexionController.VIEW);
    }

    /** Valide la saisie ; la transition attend le raccordement des notifications de Communication. */
    @FXML
    private void changeServer() {
        String ip = server_ip_field.getText().strip();
        String error = ip.isEmpty() ? "L'adresse IP est obligatoire." : ConnexionController.validatePort(server_port_field.getText());
        displayError(error);
        if (error != null) {
            return;
        }
        // TODO intégration : confirmer la nouvelle connexion avant de changer la session affichée.
        // Ne pas fermer une session réelle pour la remplacer par une connexion seulement visuelle.
        displayError("Le changement de serveur n'est pas encore disponible. La connexion actuelle est conservée.");
    }

    /** « Retour » : écran précédent. */
    @FXML
    private void back() {
        goBack(server_ip_field, HomeController.VIEW);
    }

    private void displayError(String message) {
        error_label.setText(message);
        error_label.setVisible(message != null);
    }
}
