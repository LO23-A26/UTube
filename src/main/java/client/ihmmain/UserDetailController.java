package client.ihmmain;

import common.dataclasses.ContactList;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;

/**
 * Displays the selected user's profile in the content area of the main view.
 *
 * @author Santiago Avellaneda Rodriguez
 * @version 1.0
 */
public class UserDetailController extends Controller {

    private static final int INITIALS_LENGTH = 2;
    private static final int CARD_SPACING = 6;
    private static final int PROFILE_TIMEOUT_SECONDS = 10;
    private static final String LOADING_TEXT = "Chargement du profil…";
    private static final String UNAVAILABLE_TEXT = "Profil indisponible";

    @FXML
    private Label avatar_label;
    @FXML
    private Label pseudo_label;
    @FXML
    private Label identity_label;
    @FXML
    private MenuButton add_to_list_button;
    @FXML
    private TilePane videos_container;
    @FXML
    private Label empty_label;

    private UserFull profile;
    private UserLight awaited_user;
    private PauseTransition profile_timeout;

    /**
     * Displays the selected user's identity while waiting for the profile.
     *
     * @param user the user whose profile is expected
     */
    public void displayLoading(UserLight user) {
        awaited_user = user;
        profile = null;
        showPseudo(user.getPseudo());
        identity_label.setText(LOADING_TEXT);
        videos_container.getChildren().clear();
        empty_label.setVisible(false);
        fillListMenu();
        stopProfileTimeout();
        profile_timeout = new PauseTransition(Duration.seconds(PROFILE_TIMEOUT_SECONDS));
        profile_timeout.setOnFinished(event -> showUnavailable());
        profile_timeout.play();
    }

    /**
     * Checks whether the received profile matches the expected user.
     *
     * @param awaited_user the user whose profile is expected
     * @param profile the received profile, or null if unavailable
     * @return true if the profile is expected
     */
    static boolean isAwaitedProfile(UserLight awaited_user, UserFull profile) {
        if (awaited_user == null) {
            return false;
        }
        if (profile == null) {
            return true;
        }
        if (profile.getSharedInfo() == null) {
            return false;
        }
        return awaited_user.getUuid().equals(profile.getSharedInfo().getUuid());
    }

    /**
     * Checks whether the view is waiting for the received profile.
     *
     * @param profile the received profile, or null if unavailable
     * @return true if the view is waiting for this profile
     */
    boolean isWaitingForProfile(UserFull profile) {
        return isAwaitedProfile(awaited_user, profile);
    }

    /**
     * Stores the selected profile and updates its displayed information.
     *
     * @param profile the user profile to display
     */
    public void displayProfile(UserFull profile) {
        if (profile == null) {
            showUnavailable();
            return;
        }
        if (awaited_user != null && !isAwaitedProfile(awaited_user, profile)) {
            return;
        }
        stopProfileTimeout();
        this.profile = profile;
        awaited_user = null;
        refreshView();
    }

    /**
     * Shows that the expected profile is unavailable while preserving the user's identity.
     */
    private void showUnavailable() {
        if (awaited_user != null && profile == null) {
            stopProfileTimeout();
            identity_label.setText(UNAVAILABLE_TEXT);
            videos_container.getChildren().clear();
            empty_label.setVisible(false);
        }
    }

    /** Stops the profile timeout if it exists. */
    private void stopProfileTimeout() {
        if (profile_timeout != null) {
            profile_timeout.stop();
        }
    }

    /** Updates the profile header once a profile has been provided. */
    @Override
    public void refreshView() {
        if (profile == null) {
            return;
        }
        showPseudo(profile.getSharedInfo().getPseudo());
        identity_label.setText(formatIdentity(profile.getFirstName(), profile.getLastName(),
                profile.getBirthDate(), LocalDate.now()));
        fillListMenu();
        displayVideos(profile.getSharedVideos());
    }

    /**
     * Displays the user's pseudo and avatar initials.
     *
     * @param pseudo the user's pseudo
     */
    private void showPseudo(String pseudo) {
        pseudo_label.setText(pseudo);
        avatar_label.setText(pseudo.substring(0, Math.min(INITIALS_LENGTH, pseudo.length())).toUpperCase());
    }

    /** Fills the menu with the current user's contact lists or a disabled empty message. */
    private void fillListMenu() {
        add_to_list_button.getItems().clear();
        List<ContactList> contact_lists = viewManager.getIData().getContactLists();
        if (contact_lists.isEmpty()) {
            MenuItem empty_item = new MenuItem("Aucune liste");
            empty_item.setDisable(true);
            add_to_list_button.getItems().add(empty_item);
            return;
        }
        for (ContactList list : contact_lists) {
            // TODO: Choosing a list should call addContactToList(list_id, user_id), but ContactList does not expose its id yet.
            add_to_list_button.getItems().add(new MenuItem(list.getListName()));
        }
    }

    /**
     * Replaces the displayed video cards and shows an empty message when needed.
     *
     * @param videos the shared videos to display, or null if none are available
     */
    private void displayVideos(Set<VideoLight> videos) {
        videos_container.getChildren().clear();
        if (videos == null || videos.isEmpty()) {
            empty_label.setVisible(true);
            return;
        }
        empty_label.setVisible(false);
        for (VideoLight video : videos) {
            videos_container.getChildren().add(createVideoCard(video));
        }
    }

    /**
     * Creates a card showing a shared video's title and tags.
     *
     * @param video the video to display
     * @return the video card
     */
    private VBox createVideoCard(VideoLight video) {
        Label title = new Label(video.getTitle());
        title.getStyleClass().add("label-strong");
        title.setStyle("-fx-font-size: 15px;");
        title.setTooltip(new Tooltip(video.getTitle()));
        Label tags = new Label("Tags : " + HomeController.formatTags(video.getTags()));
        tags.getStyleClass().add("text-small");
        // TODO: VideoLight has no rating; omit the mockup's "★ x/5" line until the team decides its source.
        VBox card = new VBox(CARD_SPACING, title, tags);
        card.getStyleClass().addAll("card", "clickable");
        card.setOnMouseClicked(event -> openVideoDetails(video));
        return card;
    }

    /**
     * Opens the details screen of the clicked video in the content area of the main view.
     *
     * @param video the video whose details are opened
     */
    private void openVideoDetails(VideoLight video) {
        MainController main_controller = viewManager.getController(MainController.class);
        if (main_controller != null) {
            main_controller.openVideoDetails(video);
        }
    }

    /**
     * Formats the user's full name and age when a birth date is available.
     *
     * @param first_name the user's first name
     * @param last_name the user's last name
     * @param birth_date the user's birth date, or null if unknown
     * @param today the date used to calculate the age
     * @return the last name followed by the first name and the age if known
     */
    static String formatIdentity(String first_name, String last_name, LocalDate birth_date, LocalDate today) {
        String full_name = last_name + " " + first_name;
        if (birth_date == null) {
            return full_name;
        }
        int age = Period.between(birth_date, today).getYears();
        return full_name + " · " + age + " ans";
    }

    /** Returns to the home screen and reselects the "Accueil" tab. */
    @FXML
    private void goBack() {
        viewManager.getController(MainController.class).backToHome();
    }
}
