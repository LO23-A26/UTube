package common.interfaces.IHMShare;

import common.dataclasses.VideoLight;

/**
 * Interface for IHM-Main to call IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface Interface_IHMMain_Calls_IHMShare {

    /**
     * Launch the video in the IHM-Share
     *
     * @param video (VideoLight) data of the video to read
     */
    public void launchVideo(VideoLight video);
}