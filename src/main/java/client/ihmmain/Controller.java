package client.ihmmain;

/**
 * Common base class of all the IHM Main controllers.
 */
public abstract class Controller {

    protected IHMMainManager viewManager;

    void setViewManager(IHMMainManager viewManager) {
        this.viewManager = viewManager;
    }

    /** Called by the {@link IHMMainManager} right after the view is displayed. */
    public abstract void refreshView();
}
