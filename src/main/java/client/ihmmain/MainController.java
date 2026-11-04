package client.ihmmain;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controller of the main view of the logged-in screens: header, connected users
 * panel on the right, and content area in which the {@link IHMMainManager}
 * displays the current screen (Home, Profile, IHM-Share screens...).
 */
public class MainController extends Controller {

    @FXML
    private StackPane content_pane;
    @FXML
    private VBox users_panel;
    @FXML
    private VBox users_container;

    @Override
    public void refreshView() {
        // Placeholder users until Data provides the list (getIData().getConnectedUsers())
        displayConnectedUsers(List.of("alice", "bastien", "camille", "dylan", "emma"));
    }

    /** Replaces the screen displayed in the content area. */
    void setContent(Parent content) {
        content_pane.getChildren().setAll(content);
    }

    /** Users icon of the header and close button of the panel: shows or hides the panel. */
    @FXML
    private void toggleUsersPanel() {
        users_panel.setVisible(!users_panel.isVisible());
    }

    /** "Se déconnecter" (log out) button of the header: back to the login screen (mockup 01). */
    @FXML
    private void logout() {
        // no server logout yet, call the Comm interface here
        viewManager.changeView(IHMMainManager.LOGIN_VIEW);
    }

    private void displayConnectedUsers(List<String> pseudos) {
        Label group_title = new Label("Connecté·e·s");
        group_title.getStyleClass().add("list-title");
        users_container.getChildren().setAll(group_title);
        for (String pseudo : pseudos) {
            users_container.getChildren().add(createUserRow(pseudo));
        }
    }

    private static HBox createUserRow(String pseudo) {
        Label avatar = new Label(pseudo.substring(0, Math.min(2, pseudo.length())).toUpperCase());
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(32, 32);
        avatar.setPrefSize(32, 32);
        Label online = new Label("(en ligne)");
        online.getStyleClass().add("text-secondary");
        online.setStyle("-fx-font-style: italic;");
        HBox row = new HBox(avatar, new Label(pseudo), online);
        row.getStyleClass().add("user-row");
        return row;
    }
}
