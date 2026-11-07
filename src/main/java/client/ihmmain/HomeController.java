package client.ihmmain;

import common.dataclasses.VideoLight;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Set;

/**
 * Controller of the home screen (mockup 04): videos available for download,
 * displayed in the content area of the main view.
 */
public class HomeController extends Controller {

    @FXML
    private TilePane videos_container;
    @FXML
    private Label empty_label;

    @Override
    public void refreshView() {
        // Placeholder videos until Data provides the videos of the connected users
        displayVideos(List.of(
                new VideoLight(null, "Présentation UTube", "alice", Set.of("lo23", "projet")),
                new VideoLight(null, "Tutoriel JavaFX : FXML et contrôleurs", "bastien", Set.of("java", "javafx", "tuto")),
                new VideoLight(null, "Git : le rebase", "camille", Set.of("git", "tuto")),
                new VideoLight(null, "Le modèle MVC", "dylan", Set.of("conception", "mvc")),
                new VideoLight(null, "Visite du campus", "emma", Set.of("utc", "campus")),
                new VideoLight(null, "Concert de l'UTC", "alice", Set.of("musique")),
                new VideoLight(null, "Recette de crêpes", "bastien", Set.of("cuisine")),
                new VideoLight(null, "Soirée d'intégration", "camille", Set.of()))
        );
    }

    /** Replaces the displayed cards with the cards of the given videos. */
    private void displayVideos(List<VideoLight> videos) {
        videos_container.getChildren().setAll(videos.stream().map(HomeController::createVideoCard).toList());
        empty_label.setVisible(videos.isEmpty());
    }

    /**
     * Fully clickable video card (style guide 8.1): title, creator, tags, chevron.
     * All cards keep the same height: a title that is too long is truncated with "…"
     * and the tooltip shows it in full.
     */
    private static VBox createVideoCard(VideoLight video) {
        Label title = new Label(video.getTitle());
        title.getStyleClass().add("label-strong");
        title.setStyle("-fx-font-size: 15px;");
        title.setTooltip(new Tooltip(video.getTitle()));
        Label creator = new Label(video.getCreator());
        creator.getStyleClass().add("text-secondary");
        Label tags = new Label("Tags : " + formatTags(video.getTags()));
        tags.getStyleClass().add("text-small");
        VBox texts = new VBox(6, title, creator, tags);
        // Lets the texts shrink up to the chevron instead of overflowing the card
        texts.setMinWidth(0);
        HBox.setHgrow(texts, Priority.ALWAYS);

        ImageView chevron = new ImageView(new Image(
                HomeController.class.getResource("/assets/icons/chevron.png").toExternalForm()));
        HBox row = new HBox(8, texts, chevron);
        row.setAlignment(Pos.CENTER_LEFT);

        // Clicking will open the video details (05), handled by IHM-Share
        VBox card = new VBox(row);
        card.getStyleClass().addAll("card", "clickable");
        return card;
    }

    /** Tags sorted alphabetically and separated by commas, or "aucun" if there are none. */
    static String formatTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "aucun";
        }
        return String.join(", ", tags.stream().sorted().toList());
    }
}
