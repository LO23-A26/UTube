package common.interfaces.IHMShare;
/**
 * Interface for IHM-Communication to call IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface InterfaceCommunicationCallsIHMShare {

    /**
     * Return the parameters of the video
     *
     * @param videoDetails (VideoFull) data of the video to read
     */
    void displayVideoDetails(VideoFull videoDetails);
    /**
    * Information that the download is done
    * @param videoPath (str)
    */
   void downloadDone(String videoPath);
}