package client.ihmshare;

import common.dataclasses.VideoFull;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import javafx.application.Platform;

/**
 * Implementation of the Communication calls IHM Share Interface.
 * The calls come from the network threads, so the display is done on the JavaFX thread.
 *
 * @author IHM-Share
 * @version 1.1
 */
public class CommunicationCallsIHMShareImpl implements InterfaceCommunicationCallsIHMShare {

    private IHMShareCore core;

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the parent object
     */
    public CommunicationCallsIHMShareImpl(IHMShareCore core) {
        this.core = core;
    }

    /**
     * Display the details of a remote video, received from its owner
     *
     * @param video_details (VideoFull) data of the video to read
     */
    @Override
    public void displayVideoDetails(VideoFull video_details) {
        Platform.runLater(() -> {
            DisplayVideoController controller = core.getControllerDisplayVideo();
            if (controller != null) {
                controller.displayVideoDetails(video_details);
            }
        });
    }

    /**
     * Information that the download is done
     *
     * @param video_path (String) local path of the downloaded video
     */
    @Override
    public void downloadDone(String video_path) {
        Platform.runLater(() -> core.getDownloadVideo().downloadDone(video_path));
    }
}
