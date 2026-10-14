
/**
 * Interface pour que IHM-Main puisse appeler IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface InterfaceIHMMainCallsIHMShare {

    /**
     * Launch the video in the IHM-Share
     *
     * @param video (VideoLight) données de la vidéo à lire
     */
    void launchVideo(VideoLight video);
}