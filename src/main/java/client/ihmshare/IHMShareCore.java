package client.ihmshare;

import client.ihmmain.IHMMainManager;
import common.interfaces.comm.InterfaceIHMShare_calls_Comm;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;

/**
 * Core of the IHM Share component. Only references the elements of the module so that
 * they can call each other: the interfaces used to call the other components
 * (Communication, Data), the implementations of the interfaces exposed to these
 * components, the controllers and the view manager shared with IHM-Main.
 * <p>
 * From an interface implementation: {@code core.getControllerDisplayVideo().displayVideoDetails(...)}.
 * From a controller: {@code core.getInterfaceFromData().getVideo(...)}.
 *
 * @author IHM-Share
 * @version 2.0
 */
public class IHMShareCore {

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

    // ---------- Getters and setters ----------

    /**
     * Set the view manager shared with IHM-Main, used to display the IHM-Share screens
     * in the central area of the connected layout and to go back to the previous screen
     *
     * @param view_manager (IHMMainManager) view manager of the application
     */
    public void setViewManager(IHMMainManager view_manager) {
        this.view_manager = view_manager;
    }

    /**
     * Get the view manager shared with IHM-Main
     *
     * @return (IHMMainManager) view manager of the application
     */
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

    /**
     * Set the controller of the video detail view currently displayed (screens 05, 05b and 05c),
     * so that the answers of the Communication component (video details, download done)
     * can be displayed in it
     *
     * @param ctrl (DisplayVideoController) controller of the video detail view
     */
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
