
/**
 * Interface pour que IHM-Main puisse appeler IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface InterfaceIHMMainCallsIHMShare {

    /**
     * Lance la lecture de la vidéo.
     *
     * @param video (VideoLight) données de la vidéo à lire
     */
    void launchVideo(VideoLight video);
}