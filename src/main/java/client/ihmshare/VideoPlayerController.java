package client.ihmshare;

import common.dataclasses.VideoFormat;
import common.dataclasses.VideoFull;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Controller of the video player (06).
 * <p>
 * TODO: real playback needs the javafx-media module (MediaPlayer + MediaView in screen_pane),
 * which is not a dependency of the project yet. For now the view checks the file and shows the controls.
 *
 * @author IHM-Share
 * @author Romain Tesseyre
 * @version 1.1
 */
public class VideoPlayerController extends ShareController {

    public static final String VIEW = "/client/ihmshare/video-player-view.fxml";

    private static final String NOT_AVAILABLE = "La vidéo n'est pas disponible sur cette machine.";

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
    /** True if the file of the video exists and can be read by the player. */
    private boolean playable;

    /**
     * Load the local video to play
     *
     * @param video (VideoFull) downloaded or shared video, with its local file path
     */
    public void loadVideo(VideoFull video) {
        this.video = video;
        this.playing = false;
        String error = checkPlayable(video.getFilePath());
        playable = error == null;
        if (error != null) {
            showError(error);
        }
        displayProgress(Duration.ZERO, Duration.UNKNOWN);
        refreshView();
        // TODO: once javafx-media is a dependency, create the Media, the MediaPlayer and a MediaView in screen_pane,
        //  then call displayProgress from the listener of the current time of the player
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
        if (!playable) {
            return;
        }
        playing = !playing;
        // TODO: play() or pause() the MediaPlayer once javafx-media is a dependency
        refreshView();
    }

    /** "Retour": back to the video detail (05b or 05c). */
    @FXML
    private void goBack() {
        playing = false;
        // TODO: stop() then dispose() the MediaPlayer once javafx-media is a dependency
        core.goBack();
    }

    /**
     * Update the progress bar and the "elapsed / total" time label
     *
     * @param current (Duration) elapsed time
     * @param total   (Duration) total duration of the video, unknown before the video is loaded
     */
    private void displayProgress(Duration current, Duration total) {
        double progress = 0;
        if (current != null && total != null && !total.isUnknown() && !total.isIndefinite() && total.toMillis() > 0) {
            progress = Math.min(1, Math.max(0, current.toMillis() / total.toMillis()));
        }
        progress_bar.setProgress(progress);
        time_label.setText(formatTime(current) + " / " + formatTime(total));
    }

    /**
     * Check that the video can be played on this machine
     *
     * @param file_path (String) local path of the video file
     * @return (String) the error message to display, or null if the video can be played
     */
    static String checkPlayable(String file_path) {
        if (file_path == null || file_path.isBlank()) {
            return NOT_AVAILABLE;
        }
        Path file = Path.of(file_path);
        if (!Files.isRegularFile(file)) {
            return NOT_AVAILABLE;
        }
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!name.endsWith("." + VideoFormat.mp4.name())) {
            return "Ce format vidéo ne peut pas être lu (MP4 uniquement).";
        }
        return null;
    }

    /**
     * Format a duration as "mm:ss"
     *
     * @param time (Duration) the duration to format
     * @return (String) e.g. "01:23" for 83 seconds, "00:00" if the duration is unknown
     */
    static String formatTime(Duration time) {
        if (time == null || time.isUnknown() || time.isIndefinite()) {
            return "00:00";
        }
        long seconds = (long) Math.floor(time.toSeconds());
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
