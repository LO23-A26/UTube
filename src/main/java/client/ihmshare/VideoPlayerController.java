package client.ihmshare;

import common.dataclasses.VideoFull;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Controller of the video player (06).
 * <p>
 * TODO: real playback needs the javafx-media module (MediaPlayer + MediaView in screen_pane),
 * which is not a dependency of the project yet. For now the view only shows the controls.
 *
 * @author IHM-Share
 * @version 1.0
 */
public class VideoPlayerController extends ShareController {

    @FXML
    private StackPane screen_pane;
    @FXML
    private ImageView play_icon;
    @FXML
    private Button play_button;
    @FXML
    private ProgressBar progress_bar;
    @FXML
    private Label time_label;
    @FXML
    private Label title_label;

    private VideoFull video;
    private boolean playing;

    /**
     * Load the local video to play
     *
     * @param video (VideoFull) downloaded or shared video, with its local file path
     */
    public void loadVideo(VideoFull video) {
        this.video = video;
        this.playing = false;
        refreshView();
    }

    @Override
    public void refreshView() {
        if (video == null) {
            return;
        }
        title_label.setText(video.getSharedInfo() != null ? video.getSharedInfo().getTitle() : "");
        play_button.setText(playing ? "Pause" : "Lecture");
        play_icon.setVisible(!playing);
    }

    @FXML
    private void togglePlay() {
        playing = !playing;
        refreshView();
    }

    /** "Retour": back to the video detail (05b or 05c). */
    @FXML
    private void goBack() {
        playing = false;
        view_manager.goBack();
    }
}
