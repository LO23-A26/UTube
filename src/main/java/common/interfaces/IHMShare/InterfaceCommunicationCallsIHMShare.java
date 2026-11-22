package common.interfaces.IHMShare;

import common.dataclasses.VideoFull;

/**
 * Interface for Communication to call IHM-Share.
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
    public void displayVideoDetails(VideoFull videoDetails);

    /**
    * Information that the download is done
    * @param videoPath (str)
    */
   public void downloadDone(String videoPath);

    /**
     * Information that the owner refused the download (access denied, error 403):
     * answer of the oldest download asked with askVideo that has not been answered yet
     *
     * @param error (Exception) error sent back by the owner
     */
    public void errorAccess(Exception error);
}