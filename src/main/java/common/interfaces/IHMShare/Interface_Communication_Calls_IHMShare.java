package common.interfaces.IHMShare;

import common.dataclasses.VideoFull;

/**
 * Interface for Communication to call IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface Interface_Communication_Calls_IHMShare {

    /**
     * Return the parameters of the video
     *
     * @param videoDetails (VideoFull) data of the video to read
     */
    public void displayVideoDetails(VideoFull videoDetails);

    /**
    * Information that the download is done
    * @param videoPath (str)
    */
   public void downloadDone(String videoPath);
}