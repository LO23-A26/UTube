package client.ihmshare;

import java.util.ArrayList;
import java.util.List;

import common.dataclasses.VideoLight;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller of the "Mes partages" screen (08): videos shared by the user.
 *
 * @author IHM-Share
 * @version 3.0
 */
public class ManageVideosController extends ShareController {

    public static final String VIEW = "/client/ihmshare/my-shares-view.fxml";

    @FXML
    private VBox videos_box;
    @FXML
    private Label empty_label;

    private List<VideoLight> videos = new ArrayList<>();

    /**
     * Set the shared videos to display
     * <p>
     * TODO: InterfaceIhmShareCallsDataClient has no method to list the videos shared by the user yet,
     * this method must be called with its result once Data provides it.
     *
     * @param videos (List of VideoLight) videos shared by the user
     */
    public void setVideos(List<VideoLight> videos) {
        this.videos = new ArrayList<>(videos);
        refreshView();
    }

    @Override
    public void refreshView() {
        videos_box.getChildren().clear();
        for (VideoLight video : videos) {
            Button delete_button = new Button("Supprimer");
            delete_button.setOnAction(event -> deleteVideo(video));
            videos_box.getChildren().add(videoRow(video.getTitle(), subtitle(video),
                    () -> core.openVideo(video), delete_button));
        }
        empty_label.setVisible(videos.isEmpty());
        empty_label.setManaged(videos.isEmpty());
    }

    /** "Ajouter un partage": opens the screen 09. */
    @FXML
    private void addVideo() {
        AddVideoController add_video = core.changeContent(AddVideoController.VIEW);
        core.setControllerAddVideo(add_video);
    }

    /**
     * "Supprimer": the video is no longer available for the other users.
     * 
     * @param video (VideoLight) the video to delete
     */
    private void deleteVideo(VideoLight video) {
        if (!confirm("Supprimer ce partage ?", "La vidéo ne sera plus disponible pour les autres utilisateur·rice·s.", "Supprimer")) {
            return;
        }
        // TODO: the sequence "Suppression vidéo" goes through IHM-Main (askDelete + deleteLocalVideo),
        // an interface from IHM-Share is needed to trigger it from here
        videos.remove(video);
        refreshView();
    }

    /**
     * Formate video details at string
     * "Tags · Créateur"
     * 
     * @param video (VideoLight) the video
     * @return (String) video details formatted
     */
    private static String subtitle(VideoLight video) {
        List<String> parts = new ArrayList<>();
        if (video.getTags() != null && !video.getTags().isEmpty()) {
            parts.add(String.join(", ", video.getTags()));
        }
        if (video.getCreator() != null && !video.getCreator().isBlank()) {
            parts.add(video.getCreator());
        }
        return String.join(" · ", parts);
    }
}
