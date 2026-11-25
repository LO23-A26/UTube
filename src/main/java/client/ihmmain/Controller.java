package client.ihmmain;

/**
 * Common base class of all the IHM Main controllers.
 */
public abstract class Controller {

    protected IHMMainManager viewManager;

    void setViewManager(IHMMainManager viewManager) {
        this.viewManager = viewManager;
    }

    /**
     * Not used by IHM-Main: its controllers initialize their view in {@code initialize()}.
     * Kept only because IHM-Share's ShareController overrides it and IHMShareCore calls it.
     * To delete once IHM-Share declares it in ShareController.
     */
    public void refreshView() {
    }
}
