package client.ihmshare;

import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;

/**
 * Controller for checking details of a video
 *
 * @author IHM-Share
 * @version 2.0
 */
public class CheckDetailsController {
    private IHMShareCore core;

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the parent object
     */
    public CheckDetailsController(IHMShareCore core) {
        this.core = core;
    }

    public void askVideoDetails(VideoLight video) {
        this.core.getInterfaceFromData().sendDetailsRequest(video.getVideoId());
    }

    public void displayVideoDetails(VideoFull video){
        // TODO
    }
}
