package common.interfaces.IHMShare;
/**
 * Interface for IHM-Main to call IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface InterfaceIHMMainCallsIHMShare {

    /**
     * Launch the video in the IHM-Share
     *
     * @param video (VideoLight) data of the video to read
     */
    void launchVideo(VideoLight video);
}