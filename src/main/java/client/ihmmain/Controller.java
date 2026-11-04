package client.ihmmain;

/**
 * Common base of every controller loaded by the {@link IHMMainManager} (fig. 6.4),
 * for both IHM-Main and IHM-Share views.
 */
public abstract class Controller {

    protected IHMMainManager view_manager;

    void setViewManager(IHMMainManager view_manager) {
        this.view_manager = view_manager;
    }

    /** Called by the {@link IHMMainManager} each time the view is displayed (first time and on "Retour"). */
    public abstract void refreshView();
}
