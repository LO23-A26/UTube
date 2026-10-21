package client.ihmmain;

import common.dataclasses.ContactList;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contrôleur de l'Accueil (fig. 6.4, maquette 04) et du layout des écrans connectés :
 * header, panneau utilisateur·rice·s et zone centrale où s'affichent les écrans 10, 11 et 12.
 * <p>
 * Les méthodes publiques « hooks » (addUserToDisplay, refreshFullDisplay, ...) correspondent aux appels
 * de DataCallsIHMMain et CommCallsIHMMain ; elles doivent être appelées sur le thread JavaFX.
 */
public class HomeController extends Controller {

    public static final String VIEW = "/client/ihmmain/home-view.fxml";

    public List<VideoLight> videos_list = new ArrayList<>();
    public List<UserLight> users_list = new ArrayList<>();
    @FXML
    public TextField research_field;

    @FXML
    private ToggleGroup nav_group;
    @FXML
    private ToggleButton home_tab;
    @FXML
    private ToggleButton friends_tab;
    @FXML
    private Label avatar_label;
    @FXML
    private StackPane content_pane;
    @FXML
    private Node home_content;
    @FXML
    private Label filters_label;
    @FXML
    private TilePane videos_container;
    @FXML
    private Label empty_label;
    @FXML
    private VBox users_panel;
    @FXML
    private VBox users_container;

    /** Contrôleur de la vue affichée au centre, null quand c'est l'Accueil. */
    private Controller content_controller;
    private String content_view;
    private String previous_content_view;

    @Override
    public void refreshView() {
        String pseudo = local_user == null ? null : local_user.getPseudo();
        avatar_label.setText(pseudo == null || pseudo.isBlank() ? "Me"
                : pseudo.substring(0, Math.min(2, pseudo.length())).toUpperCase());
        if (data_calls != null) {
            try {
                users_list = new ArrayList<>(data_calls.getConnectedUsers());
            } catch (RuntimeException e) {
                users_list = new ArrayList<>();
            }
        }
        refreshUsersPanel();
        if (content_controller == null) {
            searchVideo(research_field.getText());
        } else {
            content_controller.refreshView();
        }
    }

    // ---------- Fig. 6.4 ----------

    /**
     * Recherche locale (séquence 5.2.2 a) : la saisie est cherchée dans le titre, le créateur et les tags.
     * Un critère vide affiche toutes les vidéos disponibles.
     */
    public void searchVideo(String criteria) {
        if (content_controller != null) {
            showHomeScreen();
        }
        String text = criteria == null ? "" : criteria.strip();
        research_field.setText(text);
        filters_label.setText(text.isEmpty() ? "Filtres : titre · créateur · tags" : "Résultats pour « " + text + " »");
        if (data_calls == null) {
            showResults(videos_list);
            return;
        }
        try {
            // ponytail: null = critère ignoré, convention à confirmer avec Data
            if (text.isEmpty()) {
                showResults(data_calls.searchSharedVideos(null, null, null));
            } else {
                showResults(mergeResults(List.of(
                        data_calls.searchSharedVideos(text, null, null),
                        data_calls.searchSharedVideos(null, text, null),
                        data_calls.searchSharedVideos(null, null, parseTags(text)))));
            }
        } catch (RuntimeException e) {
            showError("Recherche impossible : " + e.getMessage());
        }
    }

    /** Affiche la grille de cartes vidéo (titre, uploader, tags). */
    public void showResults(List<VideoLight> list) {
        videos_list = new ArrayList<>(list);
        videos_container.getChildren().clear();
        for (VideoLight video : videos_list) {
            videos_container.getChildren().add(videoCard(video, true));
        }
        empty_label.setVisible(videos_list.isEmpty());
    }

    /**
     * Clic sur un pseudo : ouvre le Détail utilisateur·rice (11) en attente du profil et le demande à Data
     * (séquence 5.1.5.1). Le profil arrive par {@link #displayRemoteProfile}.
     */
    public void checkProfile(UserLight user) {
        if (user == null || !isAvailable(data_calls, "Data")) {
            return;
        }
        UserDetailController detail = showContent(UserDetailController.VIEW);
        detail.waitForProfile(user);
        try {
            data_calls.requestRemoteProfile(user, local_user);
        } catch (RuntimeException e) {
            showError("Profil indisponible : " + e.getMessage());
        }
    }

    /** Clic sur une vidéo : IHM-Share prend la main pour le détail 05 / 05b / 05c (annexe, transitions). */
    public void checkVideoDetails(VideoLight video) {
        // TODO: share_calls.launchVideo(video) quand IHMMainCallsIHMShare sera dans develop
        showError("Le détail de la vidéo est fourni par IHM-Share, qui n'est pas encore branché.");
    }

    /** Suppression d'une de ses vidéos : Data la supprime en local, Comm prévient le serveur (séquence 5.2.1.2). */
    public void deleteVideo(UUID video_id) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            data_calls.deleteLocalVideo(video_id);
            // TODO: comm_calls.askDelete(video_id) quand IHMMainCallsComm sera dans develop
        } catch (RuntimeException e) {
            showError("Suppression impossible : " + e.getMessage());
            return;
        }
        removeVideoFromList(video_id);
    }

    /** « Se déconnecter » : Comm sauvegarde le profil et ferme la session (séquence 5.1.3.1), retour à 01. */
    @FXML
    public void disconnect() {
        // TODO: comm_calls.closeSession() quand IHMMainCallsComm sera dans develop
        setLocalUser(null);
        navigate(research_field, ConnexionController.VIEW);
    }

    // ---------- Hooks de DataCallsIHMMain / CommCallsIHMMain ----------

    /** notifyNewUser (séquence 5.1.2.2) : ajoute la personne et ses vidéos à l'affichage. */
    public void addUserToDisplay(UserLight new_user, List<VideoLight> new_videos) {
        if (users_list.stream().noneMatch(u -> u.getUuid().equals(new_user.getUuid()))) {
            users_list.add(new_user);
        }
        refreshUsersPanel();
        if (new_videos != null) {
            new_videos.forEach(this::displayNewVideo);
        }
    }

    /** refreshFullDisplay (séquence 5.1.2.3) : liste complète reçue à la connexion. */
    public void refreshFullDisplay(List<UserLight> user_list, List<VideoLight> video_list) {
        users_list = new ArrayList<>(user_list);
        refreshUsersPanel();
        showResults(video_list);
    }

    /** refreshUserList (séquence 5.1.3.2) : la personne déconnectée et ses vidéos disparaissent. */
    public void refreshUserList(UserFull user_profile) {
        UUID user_id = user_profile.getSharedInfo().getUuid();
        users_list.removeIf(u -> u.getUuid().equals(user_id));
        refreshUsersPanel();
        showResults(videos_list.stream()
                .filter(v -> v.getOwner() == null || !v.getOwner().getUuid().equals(user_id))
                .toList());
    }

    /** displayNewVideo (séquence 5.2.1.1 c) : ajoute une vidéo nouvellement partagée. */
    public void displayNewVideo(VideoLight video_info) {
        if (videos_list.stream().noneMatch(v -> v.getVideoId().equals(video_info.getVideoId()))) {
            videos_list.add(video_info);
            showResults(videos_list);
        }
    }

    /** refreshSharedVideos (séquence 5.2.2 b) : catalogue local mis à jour. */
    public void refreshSharedVideos(Set<VideoLight> videos) {
        showResults(new ArrayList<>(videos));
    }

    /** removeVideoFromList (séquence 5.2.1.2) : la vidéo supprimée n'est plus proposée. */
    public void removeVideoFromList(UUID video_id) {
        videos_list.removeIf(v -> v.getVideoId().equals(video_id));
        showResults(videos_list);
    }

    /** displayRemoteProfile (séquence 5.1.5.2) : affiche le profil reçu dans le Détail utilisateur·rice. */
    public void displayRemoteProfile(UserFull heavy_profile) {
        UserDetailController detail = content_controller instanceof UserDetailController current
                ? current : showContent(UserDetailController.VIEW);
        detail.renderProfile(heavy_profile);
    }

    /** Fenêtre 08a ouverte depuis IHM-Share (écrans 05c et 08) avec l'identifiant de la vidéo. */
    public void openVideoAccessRights(UUID video_id) {
        String title = videos_list.stream().filter(v -> v.getVideoId().equals(video_id))
                .map(VideoLight::getTitle).findFirst().orElse("");
        ManageAccessDialogController access = dialog(research_field, ManageAccessDialogController.VIEW);
        access.openVideoAccessRights(video_id, title);
    }

    // ---------- Navigation dans le layout ----------

    /** Affiche une vue IHM Main dans la zone centrale, sous le header et à côté du panneau. */
    public <T extends Controller> T showContent(String view) {
        FXMLLoader loader = loader(view);
        content_pane.getChildren().setAll(load(loader));
        T controller = loader.getController();
        copyTo(controller);
        if (!view.equals(content_view)) {
            previous_content_view = content_view;
        }
        content_view = view;
        content_controller = controller;
        nav_group.selectToggle(tabOf(view));
        controller.refreshView();
        return controller;
    }

    /** Retour à l'écran affiché précédemment dans le layout (ou à l'Accueil). */
    public void goBack() {
        if (previous_content_view == null || previous_content_view.equals(content_view)) {
            showHomeScreen();
        } else {
            showContent(previous_content_view);
        }
    }

    /** Onglet Accueil et fin de modification du profil (séquence 5.1.1.2). */
    @FXML
    public void showHomeScreen() {
        previous_content_view = content_view;
        content_view = null;
        content_controller = null;
        content_pane.getChildren().setAll(home_content);
        nav_group.selectToggle(home_tab);
        searchVideo(research_field.getText());
    }

    /** Onglet Amis (séquence 5.1.6.1). */
    @FXML
    public void openContactLists() {
        showContent(ContactListsController.VIEW);
    }

    @FXML
    private void handleResearch() {
        searchVideo(research_field.getText());
    }

    /** Téléchargements (07) est un écran IHM-Share, sans appel prévu dans son interface. */
    @FXML
    private void openDownloads() {
        nav_group.selectToggle(tabOf(content_view));
        showError("L'écran Mes téléchargements est fourni par IHM-Share, qui n'est pas encore branché.");
    }

    /** Partages (08) est un écran IHM-Share, sans appel prévu dans son interface. */
    @FXML
    private void openShares() {
        // TODO: ouvrir l'écran Mes partages (08) via IHMMainCallsIHMShare quand il sera dans develop
        nav_group.selectToggle(tabOf(content_view));
        showError("L'écran Mes partages est fourni par IHM-Share, qui n'est pas encore branché.");
    }

    /** Avatar du header : Mon profil (12). */
    @FXML
    private void openMyProfile() {
        showContent(ProfileController.VIEW);
    }

    /** Icône du header et bouton × : affiche ou masque le panneau utilisateur·rice·s. */
    @FXML
    private void toggleUsersPanel() {
        users_panel.setVisible(!users_panel.isVisible());
        users_panel.setManaged(users_panel.isVisible());
    }

    /** « Gérer les serveurs » : écran Serveur (03), en carte centrée sans header ni panneau. */
    @FXML
    private void openServerSettings() {
        navigate(research_field, ServerController.VIEW);
    }

    private Toggle tabOf(String view) {
        if (ContactListsController.VIEW.equals(view)) {
            return friends_tab;
        }
        return view == null ? home_tab : null;
    }

    // ---------- Panneau utilisateur·rice·s ----------

    /** Personnes regroupées par liste de contacts (Amis, listes perso, Bloqués) avec la mention « en ligne ». */
    private void refreshUsersPanel() {
        users_container.getChildren().clear();
        List<ContactList> lists = List.of();
        if (data_calls != null) {
            try {
                lists = ContactListsController.orderLists(data_calls.getContactLists());
            } catch (RuntimeException e) {
                users_container.getChildren().add(new Label("Listes de contacts indisponibles."));
            }
        }
        Set<UUID> online = users_list.stream().map(UserLight::getUuid).collect(Collectors.toSet());
        Set<UUID> listed = new HashSet<>();
        for (ContactList list : lists) {
            Set<UserLight> contacts = ContactListsController.contactsOf(list);
            contacts.forEach(u -> listed.add(u.getUuid()));
            addUsersGroup("Liste : " + list.getListName(), contacts, online);
        }
        // Les personnes connectées hors de toute liste restent visibles (séquence 5.1.2.2).
        List<UserLight> others = users_list.stream()
                .filter(u -> !listed.contains(u.getUuid()))
                .filter(u -> local_user == null || !local_user.getUuid().equals(u.getUuid()))
                .toList();
        if (!others.isEmpty()) {
            addUsersGroup("Connecté·e·s", others, online);
        }
    }

    private void addUsersGroup(String title, Iterable<UserLight> users, Set<UUID> online) {
        Label group = new Label(title);
        group.getStyleClass().add("list-title");
        users_container.getChildren().add(group);
        for (UserLight user : users) {
            Hyperlink pseudo = new Hyperlink(user.getPseudo());
            pseudo.setStyle("-fx-underline: false;");
            pseudo.setOnAction(e -> checkProfile(user));
            HBox row = new HBox(avatar(13, user), pseudo);
            if (online.contains(user.getUuid())) {
                Label status = new Label("(en ligne)");
                status.setFont(Font.font("IBM Plex Sans", FontPosture.ITALIC, 14));
                row.getChildren().add(status);
            }
            row.getStyleClass().add("user-row");
            users_container.getChildren().add(row);
        }
    }

    // ---------- Cartes vidéo (04 et 11) ----------

    /** Carte vidéo entièrement cliquable (charte 8.1) : titre, uploader cliquable, tags, chevron. */
    VBox videoCard(VideoLight video, boolean show_uploader) {
        Label title = new Label(video.getTitle());
        title.getStyleClass().add("label-strong");
        title.setStyle("-fx-font-size: 15px;");
        VBox texts = new VBox(6, title);
        if (show_uploader && video.getOwner() != null) {
            Hyperlink uploader = new Hyperlink(video.getOwner().getPseudo());
            uploader.getStyleClass().add("secondary");
            uploader.setStyle("-fx-underline: false; -fx-font-size: 13px;");
            uploader.setOnAction(e -> checkProfile(video.getOwner()));
            uploader.addEventHandler(MouseEvent.MOUSE_CLICKED, MouseEvent::consume);
            texts.getChildren().add(uploader);
        }
        Label tags = new Label("Tags : " + formatTags(video.getTags()));
        tags.getStyleClass().add("text-small");
        texts.getChildren().add(tags);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        ImageView chevron = new ImageView(new Image(HomeController.class
                .getResource("/assets/icons/chevron.png").toExternalForm()));
        HBox row = new HBox(texts, spacer, chevron);
        row.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(row);
        card.getStyleClass().addAll("card", "clickable");
        card.setOnMouseClicked(e -> checkVideoDetails(video));
        return card;
    }

    static String formatTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "aucun";
        }
        return tags.stream().sorted().map(t -> "[" + t + "]").collect(Collectors.joining(" "));
    }

    /** Tags d'une saisie : séparés par des virgules ou des espaces, sans doublon. */
    static Set<String> parseTags(String text) {
        return Arrays.stream(text.split("[,\\s]+"))
                .map(String::strip)
                .filter(t -> !t.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /** Union des résultats de plusieurs recherches, sans doublon (même identifiant de vidéo). */
    static List<VideoLight> mergeResults(List<List<VideoLight>> results) {
        Map<UUID, VideoLight> merged = new LinkedHashMap<>();
        for (List<VideoLight> result : results) {
            if (result != null) {
                result.forEach(v -> merged.putIfAbsent(v.getVideoId(), v));
            }
        }
        return new ArrayList<>(merged.values());
    }
}
