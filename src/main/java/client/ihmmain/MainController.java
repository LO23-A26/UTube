package client.ihmmain;

import java.util.ArrayList;
import java.util.List;

import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller of the main view of the logged-in screens: header, connected users
 * panel on the right, and content area in which the {@link IHMMainManager}
 * displays the current screen (Home, Profile, IHM-Share screens...).
 */
public class MainController extends Controller {

    @FXML
    private ToggleButton home_tab;
    @FXML
    private StackPane content_pane;
    @FXML
    private VBox users_panel;
    @FXML
    private VBox users_container;

    /** Called by the manager when the main view is displayed: draws the users panel. */
    @Override
    public void refreshView() {
        refreshUserList();
    }

    /** Redraws the users panel from the users kept by the manager (filled by the Data notifications). */
    void refreshUserList() {
        List<String> pseudos = new ArrayList<>();
        for (UserLight user : viewManager.getDisplayedUsers()) {
            if (user.getPseudo() != null) pseudos.add(user.getPseudo());
        }
        displayConnectedUsers(pseudos);
    }

    /** Replaces the screen displayed in the content area. */
    void setContent(Parent content) {
        content_pane.getChildren().setAll(content);
    }

    /** "Accueil" (home) tab of the header. */
    @FXML
    private void showHome() {
        viewManager.showContent(IHMMainManager.HOME_VIEW);
        // A ToggleButton is deselected when it is clicked again
        home_tab.setSelected(true);
    }

    /** Avatar of the header: opens the "Mon profil" screen (mockup 12) in the content area. */
    @FXML
    private void showProfile() {
        viewManager.showContent(IHMMainManager.PROFILE_VIEW);
        // No tab of the header matches the profile screen
        home_tab.setSelected(false);
    }

    /** Back to the home screen from a screen without a tab (Mon profil...): also reselects the "Accueil" tab. */
    void backToHome() {
        viewManager.showContent(IHMMainManager.HOME_VIEW);
        home_tab.setSelected(true);
    }

    @FXML
    private void openDownloads(){
        InterfaceIHMMainCallsIHMShare share_calls = viewManager.getIShare();
        if (share_calls!=null){
            share_calls.openDownloads(content_pane);
        }
    }

    @FXML 
    private void openMyShares(){
        InterfaceIHMMainCallsIHMShare share_calls = viewManager.getIShare();
        if (share_calls!=null){
            share_calls.openMyShares(content_pane);
        }
    }

    /**
     * Opens the details screen of a video (screen 05, handled by IHM-Share) in the content area.
     * Does nothing if IHM-Share is not available.
     */
    void openVideoDetails(VideoLight video) {
        InterfaceIHMMainCallsIHMShare share_calls = viewManager.getIShare();
        if (share_calls != null && video != null) {
            share_calls.launchVideo(content_pane, video);
        }
    }

    /** Users icon of the header and close button of the panel: shows or hides the panel. */
    @FXML
    private void toggleUsersPanel() {
        users_panel.setVisible(!users_panel.isVisible());
    }

    /** "Se déconnecter" (log out) button of the header: back to the login screen (mockup 01). */
    @FXML
    private void logout() {
        if (viewManager.getIComm() != null) {
            viewManager.getIComm().closeSession();
        }
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
