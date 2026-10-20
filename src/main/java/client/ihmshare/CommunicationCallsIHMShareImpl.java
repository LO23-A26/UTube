package client.ihmshare;

/**
 * Implementation of the Communication calls IHM Share Interface
 *
 * @author IHM-Share 
 * @version 1.0
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
     * Return the details of the video
     *
     * @param video_details (VideoFull) data of the video to read
     */
    public void displayVideoDetails(VideoFull video_details){
        // TODO
    }

    /**
    * Information that the download is done
    * 
    * @param videoPath (str)
    */
    public void downloadDone(String videoPath){
        // TODO
   }
}