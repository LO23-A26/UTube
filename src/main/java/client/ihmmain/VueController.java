package client.ihmmain;

import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller of the connected layout (header + central area).
 */
public class VueController extends Controller {

    @FXML
    private Label statusLabel;

    @FXML
    private void initialize() {
        refreshView();
    }

    @Override
    public void refreshView() {
        statusLabel.setText("Bienvenue sur UTube");
    }

    /** "Accueil" tab of the header (04). */
    @FXML
    private void openHome() {
        view_manager.showHome();
    }

    /** "Téléchargements" tab of the header: screen 07, provided by IHM-Share. */
    @FXML
    private void openDownloads() {
        InterfaceIHMMainCallsIHMShare share_calls = view_manager.getInterfaceCallsShare();
        if (share_calls != null) {
            share_calls.openDownloads();
        }
    }

    /** "Partages" tab of the header: screen 08, provided by IHM-Share. */
    @FXML
    private void openShares() {
        InterfaceIHMMainCallsIHMShare share_calls = view_manager.getInterfaceCallsShare();
        if (share_calls != null) {
            share_calls.openMyShares();
        }
    }

    /** "Se déconnecter" in the header: back to the login screen (01). */
    @FXML
    private void logout() {
        // TODO: no server disconnection yet, call the Comm interface here
        view_manager.changeView(IHMMainManager.LOGIN_VIEW);
    }
}
