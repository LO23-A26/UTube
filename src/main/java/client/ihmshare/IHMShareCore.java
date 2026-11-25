package client.ihmshare;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import client.ihmmain.IHMMainManager;
import common.dataclasses.VideoLight;
import common.interfaces.comm.InterfaceIHMShareCallsComm;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

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

    private InterfaceIHMShareCallsComm interface_from_communication;
    private InterfaceIhmShareCallsDataClient interface_from_data;
    // TODO: interface_from_ihm_main, once IHM-Main publishes its interface for IHM-Share in common
    // (needed to open the window 08a "Gérer les accès")

    private CommunicationCallsIHMShareImpl impl_interface_for_communication;
    private IHMMainCallsIHMShareImpl impl_interface_for_ihm_main;
    private DataCallsIHMShareImpl impl_interface_for_data;

    private IHMMainManager view_manager;
    private DisplayVideoController ptr_display_video_controller;
    private AddVideoController ptr_add_video_controller;
    private DownloadsController ptr_downloads_controller;

    private final DownloadVideo download_video;

    /**
     * A screen displayed in the container: the nodes put in it and, for an IHM-Share screen,
     * its FXML path and controller. The content left by IHM-Main has no path nor controller.
     *
     * @param fxml_path  (String) path of the FXML file of the screen, null for the content left by IHM-Main
     * @param nodes      (List<Node>) nodes displayed in the container for this screen
     * @param controller (ShareController) controller of the screen, null for the content left by IHM-Main
     */
    private record Screen(String fxml_path, List<Node> nodes, ShareController controller) {
    }

    /** StackPane received from IHM-Main, in which the IHM-Share screens are displayed. */
    private StackPane container;
    private Screen current_screen;
    /** Previous screens, most recent first, for the "Retour" buttons. */
    private final Deque<Screen> history = new ArrayDeque<>();

    /**
     * Class constructor. Creates the implementations of the interfaces exposed
     * to the other components, each one keeping a pointer to this object
     */
    public IHMShareCore() {
        this.impl_interface_for_communication = new CommunicationCallsIHMShareImpl(this);
        this.impl_interface_for_ihm_main = new IHMMainCallsIHMShareImpl(this);
        this.impl_interface_for_data = new DataCallsIHMShareImpl(this);
        this.download_video = new DownloadVideo(this);
    }

    /**
     * Get the manager of the downloads (requests in progress and downloaded videos)
     *
     * @return (DownloadVideo) manager of the downloads
     */
    public DownloadVideo getDownloadVideo() {
        return this.download_video;
    }

    // ---------- Navigation in the StackPane given by IHM-Main ----------

    /**
     * Entry point from IHM-Main: displays an IHM-Share screen in the given container.
     * The previous content of the container is kept, so that "Retour" can put it back.
     *
     * @param container (StackPane) area of IHM-Main in which the screen is displayed
     * @param fxml_path (String) path of the FXML file of the screen
     * @return (C) controller of the displayed screen, to give it its data
     */
    public <C extends ShareController> C openScreen(StackPane container, String fxml_path) {
        this.container = container;
        history.clear();
        history.push(new Screen(null, new ArrayList<>(container.getChildren()), null));
        return show(fxml_path);
    }

    /**
     * Displays another IHM-Share screen in the same container, keeping the current one
     * in the history for the "Retour" button
     *
     * @param fxml_path (String) path of the FXML file of the screen
     * @return (C) controller of the displayed screen, to give it its data
     */
    public <C extends ShareController> C changeContent(String fxml_path) {
        if (current_screen != null) {
            history.push(current_screen);
        }
        return show(fxml_path);
    }

    /**
     * Opens the video detail (05, 05b or 05c) from another IHM-Share screen (07, 08),
     * keeping that screen in the history for "Retour"
     *
     * @param video (VideoLight) data of the video to display
     */
    public void openVideo(VideoLight video) {
        DisplayVideoController controller = changeContent(DisplayVideoController.VIEW);
        setControllerDisplayVideo(controller);
        controller.displayVideo(video);
    }

    /**
     * "Retour": displays the previous screen again, with its state. From the first
     * IHM-Share screen, puts back the content IHM-Main had in the container.
     * Does nothing if the history is empty.
     */
    public void goBack() {
        if (!history.isEmpty()) {
            restore(history.pop());
        }
    }

    /**
     * Goes back to a given screen (e.g. 09 "Ajouter un partage" → 08 "Mes partages").
     * If the screen is in the history, it is restored with its state; otherwise it is loaded again.
     *
     * @param fxml_path (String) path of the FXML file of the screen
     * @return (C) controller of the displayed screen
     */
    @SuppressWarnings("unchecked") // the screen with this FXML path was loaded with a controller of type C
    public <C extends ShareController> C goBackTo(String fxml_path) {
        boolean in_history = history.stream().anyMatch(screen -> fxml_path.equals(screen.fxml_path()));
        if (!in_history) {
            return show(fxml_path);
        }
        Screen target = history.pop();
        while (!fxml_path.equals(target.fxml_path())) {
            target = history.pop();
        }
        restore(target);
        return (C) target.controller();
    }

    /**
     * Loads an FXML view, displays it in the container in place of the current content,
     * links its controller to this core and asks the controller to refresh its view
     *
     * @param fxml_path (String) path of the FXML file of the screen
     * @return (C) controller of the displayed screen
     * @throws IllegalStateException if IHM-Main has not given its container yet
     * @throws UncheckedIOException  if the FXML file cannot be loaded
     */
    private <C extends ShareController> C show(String fxml_path) {
        if (container == null) {
            throw new IllegalStateException("No container: IHM-Main must call an IHM-Share method with its StackPane first");
        }
        FXMLLoader loader = new FXMLLoader(IHMShareCore.class.getResource(fxml_path));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load the view " + fxml_path, e);
        }
        C controller = loader.getController();
        controller.setCore(this);
        container.getChildren().setAll(root);
        current_screen = new Screen(fxml_path, List.of(root), controller);
        controller.refreshView();
        return controller;
    }

    /**
     * Puts a previously displayed screen back in the container and, if it is an
     * IHM-Share screen, asks its controller to refresh its view
     *
     * @param screen (Screen) screen to display again
     */
    private void restore(Screen screen) {
        container.getChildren().setAll(screen.nodes());
        current_screen = screen.controller() == null ? null : screen;
        if (screen.controller() != null) {
            screen.controller().refreshView();
        }
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
     * @param I (InterfaceIHMShareCallsComm) interface to the Communication component
     */
    public void setInterfaceFromCommunication(InterfaceIHMShareCallsComm I) {
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
     * @return (InterfaceIHMShareCallsComm) interface to the Communication component, null if not plugged yet
     */
    public InterfaceIHMShareCallsComm getInterfaceFromCommunication() {
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

    /**
     * Set the controller of the "add video" view currently displayed, so that the answers
     * of the other components can be displayed in it
     *
     * @param ctrl (AddVideoController) controller of the add video view
     */
    public void setControllerAddVideo(AddVideoController ctrl) {
        this.ptr_add_video_controller = ctrl;
    }

    /**
     * Get the controller of the video detail view currently displayed (screens 05, 05b and 05c)
     *
     * @return (DisplayVideoController) controller of the video detail view, null if none is displayed
     */
    public DisplayVideoController getControllerDisplayVideo() {
        return this.ptr_display_video_controller;
    }

    /**
     * Get the controller of the "add video" view currently displayed
     *
     * @return (AddVideoController) controller of the add video view, null if none is displayed
     */
    public AddVideoController getControllerAddVideo() {
        return this.ptr_add_video_controller;
    }

    /**
     * Set the controller of the "Mes téléchargements" screen (07) currently displayed,
     * so that it is refreshed when a download is done
     *
     * @param ctrl (DownloadsController) controller of the screen 07
     */
    public void setControllerDownloads(DownloadsController ctrl) {
        this.ptr_downloads_controller = ctrl;
    }

    public DownloadsController getControllerDownloads() {
        return this.ptr_downloads_controller;
    }
}
