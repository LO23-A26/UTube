package client.ihmmain;

/**
 * Base commune de tous les contrôleurs de l'IHM Main.
 */
public abstract class Controller {

    protected IHMMainManager viewManager;

    void setViewManager(IHMMainManager viewManager) {
        this.viewManager = viewManager;
    }

    /** Appelée par l'{@link IHMMainManager} juste après que la vue a été affichée. */
    public abstract void refreshView();
}
