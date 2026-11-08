package client.ihmshare;

import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.UserLight;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.comm.InterfaceIHMShare_calls_Comm;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Controller of the video detail view: screens 05 (not downloaded), 05b (downloaded)
 * and 05c (video of the user). In JavaFX these three screens are one view whose state
 * depends on the video (§8.3).
 *
 * @author IHM-Share
 * @version 1.0
 */
public class DisplayVideoController extends ShareController {

    /** State of the view, chosen from the video (05, 05b or 05c). */
    enum State {
        NOT_DOWNLOADED, DOWNLOADED, OWNER
    }

    public static final String VIEW = "/client/ihmshare/video-detail-view.fxml";

    private static final int MAX_RATING = 5;

    @FXML
    private Label title_label;
    @FXML
    private Label info_label;
    @FXML
    private Hyperlink owner_link;
    @FXML
    private HBox average_box;
    @FXML
    private HBox average_stars;
    @FXML
    private Label average_label;
    @FXML
    private Label count_label;
    @FXML
    private VBox description_box;
    @FXML
    private Label description_label;
    @FXML
    private Button download_button;
    @FXML
    private Button play_button;
    @FXML
    private Label state_badge;
    @FXML
    private Button access_button;
    @FXML
    private Label state_label;
    @FXML
    private HBox my_rating_stars;
    @FXML
    private TextField comment_field;
    @FXML
    private VBox reviews_box;
    @FXML
    private Label no_review_label;

    private VideoLight video;
    /** Full data of the video, null until Data (local video) or Comm (remote video) gives it. */
    private VideoFull video_full;
    /** Number of stars selected in "Ma note" (0 = none). */
    private int selected_rating;

    // ---------- Calls from the core and from the other modules ----------

    /**
     * Display a video: local data are read from Data, otherwise the details are asked
     * to the owner (sequence "Voir détails vidéo") and arrive in {@link #displayVideoDetails}
     *
     * @param video (VideoLight) video selected by the user
     */
    public void displayVideo(VideoLight video) {
        this.video = video;
        this.video_full = null;
        InterfaceIhmShareCallsDataClient data = core.getInterfaceFromData();
        if (data != null) {
            VideoFull local_video = data.getVideo(video);
            if (local_video != null) {
                video_full = local_video;
            } else {
                data.sendDetailsRequest(video.getVideoId());
            }
        }
        if (video_full == null) {
            // downloaded during the session but not known by Data (yet)
            video_full = core.getDownloadVideo().getDownloaded(video.getVideoId());
        }
        initMyReview();
        refreshView();
    }

    /**
     * Details of a remote video, received from its owner
     *
     * @param video_details (VideoFull) details of the video
     */
    public void displayVideoDetails(VideoFull video_details) {
        if (video == null || video_details == null || video_details.getSharedInfo() == null
                || !video.getVideoId().equals(video_details.getSharedInfo().getVideoId())) {
            return; // details of another video, the user has already left
        }
        VideoFull downloaded = core.getDownloadVideo().getDownloaded(video.getVideoId());
        if (video_details.getFilePath() == null && downloaded != null) {
            video_details.setFilePath(downloaded.getFilePath()); // keep the local copy
        }
        video_full = video_details;
        initMyReview();
        refreshView();
    }

    /**
     * The download of a video is done: if it is the displayed video, the view goes
     * to the downloaded state (05b)
     *
     * @param video_id   (UUID) id of the downloaded video
     * @param video_path (String) local path of the downloaded video
     */
    public void downloadDone(UUID video_id, String video_path) {
        if (video == null || !video.getVideoId().equals(video_id)) {
            return; // the user is looking at another video
        }
        if (video_full == null) {
            video_full = new VideoFull(video, video_path, null, new HashMap<>(), new HashMap<>());
        } else {
            video_full.setFilePath(video_path);
        }
        refreshView();
    }

    // ---------- Display ----------

    @Override
    public void refreshView() {
        if (video == null) {
            return;
        }
        title_label.setText(video.getTitle());
        info_label.setText(infoText(video));
        UserLight owner = video.getOwner();
        owner_link.setText(owner != null ? owner.getPseudo() : "Inconnu·e");
        // TODO: open the user detail (11) of IHM-Main when an interface allows it

        // TODO: VideoLight has no description nor share date yet (dossier §8.3): hidden for now
        description_box.setVisible(false);
        description_box.setManaged(false);

        displayState(state());
        displayAverage();
        displayMyRating();
        displayReviews();
    }

    private void displayState(State state) {
        show(download_button, state == State.NOT_DOWNLOADED);
        show(play_button, state != State.NOT_DOWNLOADED);
        show(state_badge, state != State.NOT_DOWNLOADED);
        show(access_button, state == State.OWNER);
        boolean download_pending = core.getDownloadVideo().isPending(video.getVideoId());
        download_button.setDisable(download_pending);
        switch (state) {
            case NOT_DOWNLOADED -> state_label.setText(download_pending
                    ? "Téléchargement en cours…"
                    : "La vidéo sera ensuite disponible dans Mes téléchargements.");
            case DOWNLOADED -> {
                state_badge.setText("Téléchargée");
                state_label.setText("Vidéo disponible hors ligne, aussi dans Mes téléchargements.");
            }
            case OWNER -> {
                state_badge.setText("Ma vidéo");
                state_label.setText("Vidéo stockée sur votre machine, visible par les listes autorisées.");
            }
        }
    }

    private void displayAverage() {
        Map<UUID, Rating> ratings = ratings();
        Map<UUID, Comment> comments = comments();
        double average = ratings.values().stream().mapToInt(Rating::getValue).average().orElse(0);
        average_stars.getChildren().setAll(stars((int) Math.round(average), 18));
        average_label.setText(ratings.isEmpty() ? "–" : String.format(Locale.FRANCE, "%.1f / 5", average));
        count_label.setText("· " + ratings.size() + " notes · " + comments.size() + " commentaires");
    }

    private void displayMyRating() {
        my_rating_stars.getChildren().clear();
        for (int i = 1; i <= MAX_RATING; i++) {
            int value = i;
            ImageView star = star(i <= selected_rating, 24);
            star.setOnMouseClicked(event -> {
                selected_rating = value;
                displayMyRating();
            });
            star.setStyle("-fx-cursor: hand;");
            my_rating_stars.getChildren().add(star);
        }
    }

    /** Reviews list: the review of the user first ("Mon avis"), then one row per author. */
    private void displayReviews() {
        reviews_box.getChildren().clear();
        Map<UUID, Rating> ratings = ratings();
        Map<UUID, Comment> comments = comments();
        UserLight me = currentUser();
        Set<UUID> authors = new LinkedHashSet<>();
        if (me != null && (ratings.containsKey(me.getUuid()) || comments.containsKey(me.getUuid()))) {
            authors.add(me.getUuid());
        }
        authors.addAll(ratings.keySet());
        authors.addAll(comments.keySet());
        for (UUID author : authors) {
            reviews_box.getChildren().add(reviewRow(author, ratings.get(author), comments.get(author), me));
        }
        show(no_review_label, authors.isEmpty());
    }

    private Node reviewRow(UUID author, Rating rating, Comment comment, UserLight me) {
        boolean mine = me != null && me.getUuid().equals(author);
        UserLight user = rating != null ? rating.getAuthor() : comment.getAuthor();

        HBox title = new HBox(8);
        title.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Hyperlink pseudo = new Hyperlink(mine ? "Mon avis" : (user != null ? user.getPseudo() : "Inconnu·e"));
        pseudo.getStyleClass().add("strong");
        title.getChildren().add(pseudo);
        if (rating != null) {
            title.getChildren().add(stars(rating.getValue(), 12));
        } else {
            title.getChildren().add(smallLabel("Sans note"));
        }
        if (mine) {
            title.getChildren().add(action("Modifier", this::editMyReview));
            title.getChildren().add(action("Supprimer", this::deleteMyReview));
        } else if (state() == State.OWNER) {
            title.getChildren().add(action("Supprimer", () -> deleteReview(author)));
        }

        Label text;
        if (comment != null) {
            text = new Label(comment.getText());
        } else {
            text = new Label("Note sans commentaire");
            text.setStyle("-fx-font-style: italic;");
        }
        text.setWrapText(true);

        HBox row = new HBox(12, avatar(), new VBox(2, title, text));
        row.getStyleClass().add("list-row");
        return row;
    }

    // ---------- Actions ----------

    @FXML
    private void goBack() {
        view_manager.goBack();
    }

    /** "Télécharger" (sequence "Télécharger vidéo"): asked to Comm, answer in {@link #downloadDone}. */
    @FXML
    private void downloadVideo() {
        if (core.getDownloadVideo().download(video) == DownloadVideo.Request.UNAVAILABLE) {
            showError("Le téléchargement n'est pas disponible pour le moment.");
        }
        refreshView();
    }

    /** "Jouer": opens the player (06). */
    @FXML
    private void playVideo() {
        if (video_full == null || video_full.getFilePath() == null) {
            showError("La vidéo n'est pas disponible sur cette machine.");
            return;
        }
        VideoPlayerController player = attach(view_manager.changeContent(VideoPlayerController.VIEW), core);
        player.loadVideo(video_full);
    }

    /** "Gérer les accès" (05c): window 08a, provided by IHM-Main. */
    @FXML
    private void manageAccess() {
        // TODO: open the window 08a through the interface of IHM-Main (not available yet)
        showError("La gestion des accès est fournie par IHM-Main, qui n'est pas encore branché.");
    }

    /** "Noter" (sequences "Ajout note" and "Modification note"). */
    @FXML
    private void rateVideo() {
        UserLight me = currentUser();
        InterfaceIHMShare_calls_Comm comm = core.getInterfaceFromCommunication();
        if (selected_rating == 0) {
            showError("Choisissez une note entre 1 et 5 étoiles.");
            return;
        }
        if (me == null || comm == null) {
            showError("La notation n'est pas disponible pour le moment.");
            return;
        }
        Rating rating = new Rating(me, video.getVideoId(), LocalDate.now(), selected_rating);
        if (ratings().containsKey(me.getUuid())) {
            comm.editRating(rating, video.getOwner());
        } else {
            comm.addRating(rating, video.getOwner());
        }
        ratings().put(me.getUuid(), rating);
        refreshView();
    }

    /** "Publier" (sequences "Ajout commentaire" and "Modification commentaire"). */
    @FXML
    private void commentVideo() {
        UserLight me = currentUser();
        InterfaceIHMShare_calls_Comm comm = core.getInterfaceFromCommunication();
        String text = comment_field.getText().strip();
        if (text.isEmpty()) {
            return;
        }
        if (me == null || comm == null) {
            showError("Les commentaires ne sont pas disponibles pour le moment.");
            return;
        }
        Comment comment = new Comment(me, video.getVideoId(), LocalDate.now(), text);
        if (comments().containsKey(me.getUuid())) {
            comm.editComment(comment, video.getOwner());
        } else {
            comm.addComment(comment, video.getOwner());
        }
        comments().put(me.getUuid(), comment);
        refreshView();
    }

    /** "Modifier" on "Mon avis": puts the review back in the fields. */
    private void editMyReview() {
        initMyReview();
        displayMyRating();
        comment_field.requestFocus();
    }

    /** "Supprimer" on "Mon avis" (sequences "Suppression commentaire" and "Suppression note"). */
    private void deleteMyReview() {
        UserLight me = currentUser();
        InterfaceIHMShare_calls_Comm comm = core.getInterfaceFromCommunication();
        if (me == null || comm == null) {
            showError("La suppression n'est pas disponible pour le moment.");
            return;
        }
        if (!confirm("Supprimer votre avis ?", "Votre note et votre commentaire seront retirés de la vidéo.", "Supprimer")) {
            return;
        }
        Rating rating = ratings().remove(me.getUuid());
        Comment comment = comments().remove(me.getUuid());
        if (rating != null) {
            comm.deleteRating(rating, video.getOwner());
        }
        if (comment != null) {
            comm.deleteComment(comment, video.getOwner());
        }
        selected_rating = 0;
        comment_field.clear();
        refreshView();
    }

    /** "Supprimer" on the review of someone else: only for the owner (05c, use case 4.2.6bis). */
    private void deleteReview(UUID author) {
        InterfaceIhmShareCallsDataClient data = core.getInterfaceFromData();
        if (data == null) {
            showError("La suppression n'est pas disponible pour le moment.");
            return;
        }
        if (!confirm("Supprimer ce commentaire ?", "La note et le commentaire de cette personne seront retirés de la vidéo.", "Supprimer")) {
            return;
        }
        if (comments().remove(author) != null) {
            data.deleteComment(video.getVideoId(), author);
        }
        if (ratings().remove(author) != null) {
            data.deleteRating(video.getVideoId(), author);
        }
        refreshView();
    }

    // ---------- Helpers ----------

    State state() {
        UserLight me = currentUser();
        if (me != null && video.getOwner() != null && me.getUuid().equals(video.getOwner().getUuid())) {
            return State.OWNER;
        }
        if (video_full != null && video_full.getFilePath() != null && Files.exists(Path.of(video_full.getFilePath()))) {
            return State.DOWNLOADED;
        }
        return State.NOT_DOWNLOADED;
    }

    /** Fills "Ma note" and "Mon commentaire" with the existing review of the user. */
    private void initMyReview() {
        UserLight me = currentUser();
        Rating rating = me == null ? null : ratings().get(me.getUuid());
        Comment comment = me == null ? null : comments().get(me.getUuid());
        selected_rating = rating == null ? 0 : rating.getValue();
        comment_field.setText(comment == null ? "" : comment.getText());
    }

    private UserLight currentUser() {
        InterfaceIhmShareCallsDataClient data = core == null ? null : core.getInterfaceFromData();
        return data == null ? null : data.getUser();
    }

    private Map<UUID, Rating> ratings() {
        if (video_full == null) {
            return new HashMap<>();
        }
        if (video_full.getRatings() == null) {
            video_full.setRatings(new HashMap<>());
        }
        return video_full.getRatings();
    }

    private Map<UUID, Comment> comments() {
        if (video_full == null) {
            return new HashMap<>();
        }
        if (video_full.getComments() == null) {
            video_full.setComments(new HashMap<>());
        }
        return video_full.getComments();
    }

    /** "Créateur : X · Tags : [a] [b]" */
    static String infoText(VideoLight video) {
        List<String> parts = new ArrayList<>();
        if (video.getCreator() != null && !video.getCreator().isBlank()) {
            parts.add("Créateur : " + video.getCreator());
        }
        if (video.getTags() != null && !video.getTags().isEmpty()) {
            parts.add("Tags : " + String.join(" ", video.getTags().stream().map(tag -> "[" + tag + "]").toList()));
        }
        return String.join("  ·  ", parts);
    }

    static HBox stars(int filled, double size) {
        HBox box = new HBox(2);
        box.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        for (int i = 1; i <= MAX_RATING; i++) {
            box.getChildren().add(star(i <= filled, size));
        }
        return box;
    }

    private static ImageView star(boolean full, double size) {
        String icon = full ? "/assets/icons/etoile-pleine.png" : "/assets/icons/etoile-vide.png";
        ImageView star = new ImageView(new Image(DisplayVideoController.class.getResource(icon).toExternalForm()));
        star.setFitWidth(size);
        star.setFitHeight(size);
        star.setPreserveRatio(true);
        return star;
    }

    private static Label avatar() {
        Label avatar = new Label();
        avatar.getStyleClass().add("avatar");
        avatar.setMinSize(32, 32);
        avatar.setPrefSize(32, 32);
        return avatar;
    }

    private static Label smallLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("text-small");
        return label;
    }

    private static Hyperlink action(String text, Runnable action) {
        Hyperlink link = new Hyperlink(text);
        link.getStyleClass().add("secondary");
        link.setOnAction(event -> action.run());
        return link;
    }

    private static void show(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
