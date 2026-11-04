package client.ihmshare;

/**
 * Implementation of the IHM Main calls IHM Share Interface
 *
 * @author IHM-Share 
 * @version 1.0
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
     * Launch the video in the IHM-Share
     *
     * @param video (VideoLight) data of the video to read
     */
    public void launchVideo(VideoLight video){
        // TODO
    }
}