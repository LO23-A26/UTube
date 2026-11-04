package client.ihmshare;

import common.dataclasses.VideoLight;
import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;

/**
 * Implementation of the IHM Main calls IHM Share Interface
 *
 * @author IHM-Share
 * @version 1.1
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
     * @param video (VideoLight) data of the video to read
     */
    @Override
    public void launchVideo(VideoLight video) {
        DisplayVideoController controller = ShareController.attach(
                core.getViewManager().changeContent(DisplayVideoController.VIEW), core);
        core.setControllerDisplayVideo(controller);
        controller.displayVideo(video);
    }

    /**
     * Open the "Mes téléchargements" screen (07)
     */
    @Override
    public void openDownloads() {
        ShareController.attach(core.getViewManager().openTab(DownloadsController.VIEW), core);
    }

    /**
     * Open the "Mes partages" screen (08)
     */
    @Override
    public void openMyShares() {
        ShareController.attach(core.getViewManager().openTab(ManageVideosController.VIEW), core);
    }
}
