
/**
 * Interface pour que Communication puisse appeler IHM-Share.
 *
 * @author IHM-Share 
 * @version 1.0
 */
public interface InterfaceCommunicationCallsIHMShare {

    /**
     * Informe des parametres de la video
     *
     * @param videoDetails (VideoFull) données de la vidéo.
     */
    void displayVideoDetails(VideoFull videoDetails);
    /**
    * Informe que la video a étée téléchargée
    * @param videoPath (str)
    */
   void downloadDone(String videoPath);
}