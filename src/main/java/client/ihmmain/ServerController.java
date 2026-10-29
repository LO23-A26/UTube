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
        // TODO: comm_calls.closeSession() quand IHMMainCallsComm sera dans develop
        setLocalUser(null);
        navigate(server_ip_field, ConnexionController.VIEW);
    }

    /** « Se connecter » : ferme la connexion actuelle, se connecte au nouveau serveur et ramène à l'Accueil. */
    @FXML
    private void changeServer() {
        String ip = server_ip_field.getText().strip();
        String error = ip.isEmpty() ? "L'adresse IP est obligatoire." : ConnexionController.validatePort(server_port_field.getText());
        displayError(error);
        if (error != null) {
            return;
        }
        int port = Integer.parseInt(server_port_field.getText().strip());
        // TODO: comm_calls.closeSession() puis comm_calls.requestConnection(local_user, ip, port)
        //  quand IHMMainCallsComm sera dans develop
        setServer(ip, port);
        navigate(server_ip_field, HomeController.VIEW);
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
