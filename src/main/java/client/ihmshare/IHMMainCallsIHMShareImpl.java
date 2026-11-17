package client.ihmshare;

import common.dataclasses.VideoLight;
import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import javafx.scene.layout.StackPane;

/**
 * Implementation of the IHM Main calls IHM Share Interface
 *
 * @author IHM-Share
 * @version 1.2
 */
public class IHMMainCallsIHMShareImpl implements InterfaceIHMMainCallsIHMShare {

    private IHMShareCore core;

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the parent object
     */
    public IHMMainCallsIHMShareImpl(IHMShareCore core) {
        this.core = core;
    }

    /**
     * Launch the video in the IHM-Share (screen 05, 05b or 05c depending on the video)
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     * @param video     (VideoLight) data of the video to read
     */
    @Override
    public void launchVideo(StackPane container, VideoLight video) {
        DisplayVideoController controller = core.openScreen(container, DisplayVideoController.VIEW);
        core.setControllerDisplayVideo(controller);
        controller.displayVideo(video);
        // "Retour" puts back the IHM-Main content that was in the container
    }

    /**
     * Open the "Mes téléchargements" screen (07)
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     */
    @Override
    public void openDownloads(StackPane container) {
        core.openScreen(container, DownloadsController.VIEW);
    }

    /**
     * Open the "Mes partages" screen (08)
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     */
    @Override
    public void openMyShares(StackPane container) {
        core.openScreen(container, ManageVideosController.VIEW);
    }
}
