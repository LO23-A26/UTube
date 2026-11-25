package client.ihmmain;

import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

    @FXML
    private Button connectButton;
    private boolean connecting;

    @FXML
    private void connect() {
        if (connecting) return;
        String pseudo = pseudoField.getText();
        String password = passwordField.getText();
        String ip = ipField.getText();
        String port = portField.getText();
        String error = validate(pseudo, password, ip, port);
        showMessage(error);
        if (error != null) return;
        InterfaceIhmMainCallsDataClient data = viewManager.getIData();
        if (data == null) {
            showMessage("Le service de connexion est indisponible.");
            return;
        }

        setConnecting(true);
        passwordField.clear();
        Task<Boolean> request = new Task<>() {
            @Override protected Boolean call() {
                return data.requestConnection(pseudo.strip(), password, ip.strip(), Integer.parseInt(port.strip()));
            }
        };
        request.setOnSucceeded(event -> {
            setConnecting(false);
            if (Boolean.TRUE.equals(request.getValue())) {
                // Preserve integration/client navigation; Data acceptance is not a network acknowledgement.
                viewManager.showContent(IHMMainManager.HOME_VIEW);
            } else {
                showMessage("La demande de connexion a été refusée.");
            }
        });
        request.setOnFailed(event -> {
            setConnecting(false);
            String message = request.getException().getMessage();
            showMessage(message == null || message.isBlank()
                    ? "Impossible de se connecter."
                    : "Impossible de se connecter : " + message);
        });
        Thread worker = new Thread(request, "ihm-main-connection");
        worker.setDaemon(true);
        worker.start();
    }

    private void setConnecting(boolean value) {
        connecting = value;
        connectButton.setDisable(value);
        pseudoField.setDisable(value);
        passwordField.setDisable(value);
        ipField.setDisable(value);
        portField.setDisable(value);
    }

    private void showMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(message != null && !message.isBlank());
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

    /** On click on Créer un profil */
    @FXML
    private void createProfile() {
        viewManager.changeView(IHMMainManager.CREATE_PROFILE_VIEW);
    }
}
