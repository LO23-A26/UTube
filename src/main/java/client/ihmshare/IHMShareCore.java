package client.ihmshare;

import client.ihmmain.IHMMainManager;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.comm.InterfaceIHMShare_calls_Comm;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;

/**
 * Core of the IHM Share component. Keeps the interfaces used to call the other
 * components (Communication, Data), the implementations of the interfaces exposed
 * to these components, and the navigation between the IHM-Share screens.
 * <p>
 * The screens are displayed by the view manager shared with IHM-Main, in the central
 * area of the connected layout, so the header and the users panel stay visible.
 *
 * @author IHM-Share
 * @version 2.0
 */
public class IHMShareCore {

    public static final String VIDEO_DETAIL_VIEW = "/client/ihmshare/video-detail-view.fxml";
    public static final String VIDEO_PLAYER_VIEW = "/client/ihmshare/video-player-view.fxml";
    public static final String DOWNLOADS_VIEW = "/client/ihmshare/downloads-view.fxml";
    public static final String MY_SHARES_VIEW = "/client/ihmshare/my-shares-view.fxml";
    public static final String ADD_VIDEO_VIEW = "/client/ihmshare/add-video-view.fxml";

    private InterfaceIHMShare_calls_Comm interface_from_communication;
    private InterfaceIhmShareCallsDataClient interface_from_data;
    // TODO: interface_from_ihm_main, once IHM-Main publishes its interface for IHM-Share in common
    // (needed to open the window 08a "Gérer les accès")

    private CommunicationCallsIHMShareImpl impl_interface_for_communication;
    private IHMMainCallsIHMShareImpl impl_interface_for_ihm_main;
    private DataCallsIHMShareImpl impl_interface_for_data;

    private IHMMainManager view_manager;
    private DisplayVideoController ptr_display_video_controller;
    private AddVideoController ptr_add_video_controller;

    /**
     * Class constructor. Creates the implementations of the interfaces exposed
     * to the other components, each one keeping a pointer to this object
     */
    public IHMShareCore() {
        this.impl_interface_for_communication = new CommunicationCallsIHMShareImpl(this);
        this.impl_interface_for_ihm_main = new IHMMainCallsIHMShareImpl(this);
        this.impl_interface_for_data = new DataCallsIHMShareImpl(this);
    }

    // ---------- Navigation ----------

    /**
     * Open the detail of a video (05, 05b or 05c depending on the video)
     *
     * @param video (VideoLight) video to display
     */
    public void openVideoDetails(VideoLight video) {
        DisplayVideoController controller = show(view_manager.changeContent(VIDEO_DETAIL_VIEW));
        setControllerDisplayVideo(controller);
        controller.displayVideo(video);
    }

    /**
     * Open the video player (06)
     *
     * @param video (VideoFull) local video to play
     */
    public void openVideoPlayer(VideoFull video) {
        VideoPlayerController controller = show(view_manager.changeContent(VIDEO_PLAYER_VIEW));
        controller.loadVideo(video);
    }

    /**
     * Open the "Mes téléchargements" screen (07), header tab
     */
    public void openDownloads() {
        show(view_manager.openTab(DOWNLOADS_VIEW));
    }

    /**
     * Open the "Mes partages" screen (08), header tab
     */
    public void openMyShares() {
        show(view_manager.openTab(MY_SHARES_VIEW));
    }

    /**
     * Open the "Ajouter un partage" screen (09)
     *
     * @param source (VideoFull) downloaded video to share again, or null for a new video
     */
    public void openAddVideo(VideoFull source) {
        AddVideoController controller = show(view_manager.changeContent(ADD_VIDEO_VIEW));
        setControllerAddVideo(controller);
        controller.prefill(source);
    }

    /**
     * Back to "Mes partages" (08), from the screen 09 ("Partager", "Annuler" or "Retour")
     */
    public void backToMyShares() {
        show(view_manager.backTo(MY_SHARES_VIEW));
    }

    /**
     * Give the core to a freshly loaded controller and refresh it with its data
     */
    private <C extends ShareController> C show(C controller) {
        controller.setCore(this);
        controller.refreshView();
        return controller;
    }

    // ---------- Getters and setters ----------

    public void setViewManager(IHMMainManager view_manager) {
        this.view_manager = view_manager;
    }

    public IHMMainManager getViewManager() {
        return this.view_manager;
    }

    /**
     * Set the interface used by IHM Share to call the Communication component
     *
     * @param I (InterfaceIHMShare_calls_Comm) interface to the Communication component
     */
    public void setInterfaceFromCommunication(InterfaceIHMShare_calls_Comm I) {
        this.interface_from_communication = I;
    }

    /**
     * Set the interface used by IHM Share to call the Data component
     *
     * @param I (InterfaceIhmShareCallsDataClient) interface to the Data component
     */
    public void setInterfaceFromData(InterfaceIhmShareCallsDataClient I) {
        this.interface_from_data = I;
    }

    /**
     * Get the interface used by IHM Share to call the Communication component
     *
     * @return (InterfaceIHMShare_calls_Comm) interface to the Communication component, null if not plugged yet
     */
    public InterfaceIHMShare_calls_Comm getInterfaceFromCommunication() {
        return this.interface_from_communication;
    }

    /**
     * Get the interface used by IHM Share to call the Data component
     *
     * @return (InterfaceIhmShareCallsDataClient) interface to the Data component, null if not plugged yet
     */
    public InterfaceIhmShareCallsDataClient getInterfaceFromData() {
        return this.interface_from_data;
    }

    /**
     * Set the implementation of the interface exposed to the Communication component
     *
     * @param I (CommunicationCallsIHMShareImpl) implementation used by the Communication component
     */
    public void setInterfaceForCommunication(CommunicationCallsIHMShareImpl I) {
        this.impl_interface_for_communication = I;
    }

    /**
     * Set the implementation of the interface exposed to the IHM Main component
     *
     * @param I (IHMMainCallsIHMShareImpl) implementation used by the IHM Main component
     */
    public void setInterfaceForIHMMain(IHMMainCallsIHMShareImpl I) {
        this.impl_interface_for_ihm_main = I;
    }

    /**
     * Set the implementation of the interface exposed to the Data component
     *
     * @param I (DataCallsIHMShareImpl) implementation used by the Data component
     */
    public void setInterfaceForData(DataCallsIHMShareImpl I) {
        this.impl_interface_for_data = I;
    }

    /**
     * Get the implementation of the interface exposed to the Communication component
     *
     * @return (CommunicationCallsIHMShareImpl) implementation used by the Communication component
     */
    public CommunicationCallsIHMShareImpl getInterfaceForCommunication() {
        return this.impl_interface_for_communication;
    }

    /**
     * Get the implementation of the interface exposed to the IHM Main component
     *
     * @return (IHMMainCallsIHMShareImpl) implementation used by the IHM Main component
     */
    public IHMMainCallsIHMShareImpl getInterfaceForIHMMain() {
        return this.impl_interface_for_ihm_main;
    }

    /**
     * Get the implementation of the interface exposed to the Data component
     *
     * @return (DataCallsIHMShareImpl) implementation used by the Data component
     */
    public DataCallsIHMShareImpl getInterfaceForData() {
        return this.impl_interface_for_data;
    }

    public void setControllerDisplayVideo(DisplayVideoController ctrl) {
        this.ptr_display_video_controller = ctrl;
    }

    public void setControllerAddVideo(AddVideoController ctrl) {
        this.ptr_add_video_controller = ctrl;
    }

    public DisplayVideoController getControllerDisplayVideo() {
        return this.ptr_display_video_controller;
    }

    public AddVideoController getControllerAddVideo() {
        return this.ptr_add_video_controller;
    }
}
