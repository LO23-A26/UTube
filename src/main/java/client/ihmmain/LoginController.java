package client.ihmmain;

import client.data.AuthentificationFailed;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller of the login screen (mockup 01).
 */
public class LoginController extends Controller {

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

    @Override
    public void refreshView() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void connect() {
        String pseudo = pseudoField.getText();
        String password = passwordField.getText();
        String ip = ipField.getText();
        String port = portField.getText();

        String error = validate(pseudo, password, ip, port);
        errorLabel.setText(error);
        errorLabel.setVisible(error != null);
        if (error == null) {
            // no authentication or server connection yet, call the Data/Comm interfaces here once the connection process is defined
            try{
                boolean connect = viewManager.getIData().requestConnection(pseudo, password, ip, Integer.parseInt(port));
                if(connect){
                    viewManager.showContent(IHMMainManager.HOME_VIEW);
                }
            } catch (AuthentificationFailed e) {
                error = e.getMessage();
                errorLabel.setText(error);
                errorLabel.setVisible(true);
            }

        }
    }

    /** Returns the error message to display, or null if the input is valid. */
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
