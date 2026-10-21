package client.ihmshare;

/**
 * Controller for video add view
 *
 * @author IHM-Share
 * @version 2.0
 */
public class AddVideoController {
    private IHMShareCore core;

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the parent object
     */
    public AddVideoController(IHMShareCore core) {
        this.core = core;
    }

    /**
     * Check file format
     *
     * @param file_path (String) path of the video to add
     */
    public void checkFormat(String file_path){
        // TODO
    }
}