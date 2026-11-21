package common.interfaces.IHMShare;

import common.dataclasses.VideoLight;
import javafx.scene.layout.StackPane;

/**
 * Interface for IHM-Main to call IHM-Share.
 * <p>
 * Each method receives the {@link StackPane} in which IHM-Share displays its screen:
 * IHM-Share creates its view and puts its root in it with
 * {@code container.getChildren().setAll(root)}. The following IHM-Share screens
 * (video player, "Ajouter un partage", "Retour"...) are displayed in the same container.
 *
 * @author IHM-Share
 * @version 1.2
 */
public interface InterfaceIHMMainCallsIHMShare {

    /**
     * Launch the video in the IHM-Share (screen 05, 05b or 05c depending on the video).
     * "Retour" on this screen puts back the content the container had before the call.
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     * @param video     (VideoLight) data of the video to read
     */
    public void launchVideo(StackPane container, VideoLight video);

    /**
     * Open the "Mes téléchargements" screen (07), from the header
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     */
    public void openDownloads(StackPane container);

    /**
     * Open the "Mes partages" screen (08), from the header
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     */
    public void openMyShares(StackPane container);
}