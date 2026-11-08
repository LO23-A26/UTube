package client.ihmshare;

import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Controller of the "Mes téléchargements" screen (07).
 *
 * @author IHM-Share
 * @version 1.0
 */
public class DownloadsController extends ShareController {

    public static final String VIEW = "/client/ihmshare/downloads-view.fxml";

    @FXML
    private VBox videos_box;
    @FXML
    private Label empty_label;

    @Override
    public void refreshView() {
        if (core == null) {
            return;
        }
        // TODO: InterfaceIhmShareCallsDataClient has no method to list the downloaded videos yet:
        // only the videos downloaded during the session are displayed
        List<VideoLight> videos = core.getDownloadVideo().getDownloadedVideos();
        videos_box.getChildren().clear();
        for (VideoLight video : videos) {
            Button share_button = new Button("Re-partager");
            share_button.setOnAction(event -> shareAgain(video));
            Button delete_button = new Button("Supprimer");
            delete_button.setOnAction(event -> deleteVideo(video));
            String uploader = video.getOwner() != null ? video.getOwner().getPseudo() : "";
            videos_box.getChildren().add(videoRow(video.getTitle(), uploader,
                    () -> core.getInterfaceForIHMMain().launchVideo(video), share_button, delete_button));
        }
        empty_label.setVisible(videos.isEmpty());
        empty_label.setManaged(videos.isEmpty());
    }

    /** "Re-partager": opens the screen 09 with the file of the downloaded video. */
    private void shareAgain(VideoLight video) {
        InterfaceIhmShareCallsDataClient data = core.getInterfaceFromData();
        VideoFull local_video = data == null ? null : data.getVideo(video);
        if (local_video == null) {
            local_video = core.getDownloadVideo().getDownloaded(video.getVideoId());
        }
        if (local_video == null || local_video.getFilePath() == null
                || !Files.exists(Path.of(local_video.getFilePath()))) {
            showError("Le fichier de cette vidéo est introuvable.");
            return;
        }
        AddVideoController add_video = attach(view_manager.changeContent(AddVideoController.VIEW), core);
        core.setControllerAddVideo(add_video);
        add_video.prefill(local_video);
    }

    /** "Supprimer": removes the local copy, the video stays available at its owner. */
    private void deleteVideo(VideoLight video) {
        if (!confirm("Supprimer cette vidéo ?", "La copie locale sera supprimée. La vidéo reste disponible chez son propriétaire.", "Supprimer")) {
            return;
        }
        // TODO: no method in InterfaceIhmShareCallsDataClient to delete a downloaded video yet,
        // the local file is deleted here until Data provides one
        VideoFull removed = core.getDownloadVideo().removeDownloaded(video.getVideoId());
        if (removed != null && removed.getFilePath() != null) {
            try {
                Files.deleteIfExists(Path.of(removed.getFilePath()));
            } catch (IOException e) {
                showError("Le fichier local n'a pas pu être supprimé : " + removed.getFilePath());
            }
        }
        refreshView();
    }
}
