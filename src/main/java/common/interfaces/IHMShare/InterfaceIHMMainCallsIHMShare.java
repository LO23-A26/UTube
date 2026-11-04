package common.interfaces.IHMShare;

import common.dataclasses.VideoLight;

/**
 * Interface for IHM-Main to call IHM-Share.
 *
 * @author IHM-Share
 * @version 1.1
 */
public interface InterfaceIHMMainCallsIHMShare {

    /**
     * Launch the video in the IHM-Share (screen 05, 05b or 05c depending on the video)
     *
     * @param video (VideoLight) data of the video to read
     */
    public void launchVideo(VideoLight video);

    /**
     * Open the "Mes téléchargements" screen (07), from the header
     */
    public void openDownloads();

    /**
     * Open the "Mes partages" screen (08), from the header
     */
    public void openMyShares();
}
