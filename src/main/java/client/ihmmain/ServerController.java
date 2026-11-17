package client.ihmmain;

import common.interfaces.comm.InterfaceIHMMain_calls_Comm;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller of the server screen (mockup 03): shows the server the user is
 * connected to, and lets them disconnect from it or connect to another one.
 */
public class ServerController extends Controller {

    @FXML
    private Label server_label;
    @FXML
    private TextField ip_field;
    @FXML
    private TextField port_field;
    @FXML
    private Label error_label;

    @Override
    public void refreshView() {
        server_label.setText(viewManager.getServerIp() + " : " + viewManager.getServerPort());
        showError(null);
    }

    /** "Se déconnecter du serveur" (disconnect) button: closes the session and goes back to the login screen (mockup 01). */
    @FXML
    private void disconnect() {
        InterfaceIHMMain_calls_Comm comm = viewManager.getIComm();
        if (comm != null) {
            try {
                comm.closeSession();
            } catch (UnsupportedOperationException e) {
                // closeSession is not implemented by Comm yet: the user still goes back to the login screen
            }
        }
        viewManager.changeView(IHMMainManager.LOGIN_VIEW);
    }

    /** "Se connecter" (connect) button: connects to the new server, then displays the home screen (mockup 04). */
    @FXML
    private void changeServer() {
        String error = validate(ip_field.getText(), port_field.getText());
        showError(error);
        if (error != null) {
            return;
        }
        // TODO: no Data method connects the logged-in user to another server without asking for the
        //  password again (closing the current connection). Once Data provides it: call it outside the
        //  JavaFX thread, then viewManager.setServer(ip, port) and viewManager.showContent(HOME_VIEW).
        showError("Le changement de serveur n'est pas encore disponible.");
    }

    /** Displays the error message, or hides the label if it is null. */
    private void showError(String error) {
        error_label.setText(error);
        error_label.setVisible(error != null);
    }

    /** Returns the error message to display, or null if the server address is valid. */
    static String validate(String ip_address, String port) {
        if (ip_address.isBlank() || port.isBlank()) {
            return "L'adresse IP et le port sont obligatoires.";
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
