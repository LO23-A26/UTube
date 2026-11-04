package client.ihmshare;

import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller of the "Mes téléchargements" screen (07).
 *
 * @author IHM-Share
 * @version 1.0
 */
public class DownloadsController extends ShareController {

    @FXML
    private VBox videos_box;
    @FXML
    private Label empty_label;

    private List<VideoLight> videos = new ArrayList<>();

    /**
     * Set the downloaded videos to display
     * <p>
     * TODO: InterfaceIhmShareCallsDataClient has no method to list the downloaded videos yet,
     * this method must be called with its result once Data provides it.
     *
     * @param videos (List of VideoLight) downloaded videos
     */
    public void setVideos(List<VideoLight> videos) {
        this.videos = new ArrayList<>(videos);
        refreshView();
    }

    @Override
    public void refreshView() {
        videos_box.getChildren().clear();
        for (VideoLight video : videos) {
            Button share_button = new Button("Re-partager");
            share_button.setOnAction(event -> shareAgain(video));
            Button delete_button = new Button("Supprimer");
            delete_button.setOnAction(event -> deleteVideo(video));
            String uploader = video.getOwner() != null ? video.getOwner().getPseudo() : "";
            videos_box.getChildren().add(videoRow(video.getTitle(), uploader,
                    () -> core.openVideoDetails(video), share_button, delete_button));
        }
        empty_label.setVisible(videos.isEmpty());
        empty_label.setManaged(videos.isEmpty());
    }

    /** "Re-partager": opens the screen 09 with the file of the downloaded video. */
    private void shareAgain(VideoLight video) {
        InterfaceIhmShareCallsDataClient data = core.getInterfaceFromData();
        VideoFull local_video = data == null ? null : data.getVideo(video);
        if (local_video == null) {
            showError("Le fichier de cette vidéo est introuvable.");
            return;
        }
        core.openAddVideo(local_video);
    }

    /** "Supprimer": removes the local copy, the video stays available at its owner. */
    private void deleteVideo(VideoLight video) {
        if (!confirm("Supprimer cette vidéo ?", "La copie locale sera supprimée. La vidéo reste disponible chez son propriétaire.", "Supprimer")) {
            return;
        }
        // TODO: no method in InterfaceIhmShareCallsDataClient to delete a downloaded video yet
        videos.remove(video);
        refreshView();
    }
}
