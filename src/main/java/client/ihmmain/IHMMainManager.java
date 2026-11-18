package client.ihmmain;

import common.dataclasses.UserFull;
import common.dataclasses.VideoLight;
import common.interfaces.IHMMain.InterfaceCommCallsIHMMain;
import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import common.interfaces.comm.InterfaceIHMMainCallsComm;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Collection;
import java.util.UUID;

/**
 * View manager of IHM Main: loads the FXML screens, switches the displayed screen
 * and gives each {@link Controller} access to the interfaces of the other modules
 * (Data for now; Comm and IHM-Share will follow when their interfaces are merged
 * into this branch).
 */
public class IHMMainManager {

    public static final String LOGIN_VIEW = "/client/ihmmain/login-view.fxml";
    public static final String MAIN_VIEW = "/client/ihmmain/main-view.fxml";
    public static final String HOME_VIEW = "/client/ihmmain/home-view.fxml";
    public static final String STYLESHEET = "/assets/styles/utube.css";


    /** Controller instances are stored in a map whose KEY is the TYPE of the
     * subclass. So, to get the login controller, the key to use is:
     * key = LoginController.class
     * and to get the LoginController instance, just call:
     * controllers.get(LoginController.class)
     */
    private final Map<Class<? extends Controller>, Controller> controllers = new HashMap<>();
    private InterfaceIhmMainCallsDataClient dataCalls;
    private InterfaceIHMMainCallsComm commCalls;
    private InterfaceIHMMainCallsIHMShare shareCalls;
    private Scene primaryScene;
    /** Controller of the main view (header and users panel) while it is displayed, null otherwise. */
    private MainController main_controller;

    private final InterfaceDataCallsIHMMain dataNotifications = new DataCallsIHMMainImpl(this);
    private final InterfaceCommCallsIHMMain commNotifications = new CommCallsIHMMainImpl(this);
    // UI state only. Data and COM retain responsibility for storage and network indexes.
    private final Map<UUID, VideoLight> displayedVideos = new LinkedHashMap<>();
    private Alert remoteProfileDialog;

    public InterfaceDataCallsIHMMain getInterfaceForData() { return dataNotifications; }
    public InterfaceCommCallsIHMMain getInterfaceForComm() { return commNotifications; }

    List<VideoLight> getDisplayedVideos() { return List.copyOf(displayedVideos.values()); }

    void refreshUsers() {
        if (main_controller != null) main_controller.refreshView();
    }

    void addVideos(Collection<VideoLight> videos) {
        for (VideoLight video : videos) {
            if (video != null && video.getVideoId() != null) displayedVideos.put(video.getVideoId(), video);
        }
        refreshVideos();
    }

    void replaceVideos(Collection<VideoLight> videos) {
        displayedVideos.clear();
        addVideos(videos);
    }

    void removeVideo(UUID id) {
        displayedVideos.remove(id);
        refreshVideos();
    }

    void removeUserVideos(UUID userId) {
        displayedVideos.values().removeIf(video -> video.getOwner() != null
                && userId.equals(video.getOwner().getUuid()));
        refreshVideos();
    }

    private void refreshVideos() {
        HomeController home = getController(HomeController.class);
        if (main_controller != null && home != null) home.refreshView();
    }

    /** Displays only the received profile fields, never credentials or local collections. */
    void displayRemoteProfile(UserFull profile) {
        if (remoteProfileDialog == null) {
            remoteProfileDialog = new Alert(Alert.AlertType.INFORMATION);
            remoteProfileDialog.setTitle("Profil utilisateur");
            if (primaryScene != null && primaryScene.getWindow() != null) {
                remoteProfileDialog.initOwner(primaryScene.getWindow());
            }
        }
        if (profile == null || profile.getSharedInfo() == null) {
            remoteProfileDialog.setHeaderText("Profil indisponible");
            remoteProfileDialog.setContentText("Le profil demandé n'a pas été fourni.");
        } else {
            remoteProfileDialog.setHeaderText(profile.getSharedInfo().getPseudo());
            remoteProfileDialog.setContentText("Prénom : " + profileField(profile.getFirstName())
                    + "\nNom : " + profileField(profile.getLastName())
                    + "\nDate de naissance : " + profileField(profile.getBirthDate()));
        }
        remoteProfileDialog.show();
    }

    private static String profileField(Object value) {
        return value == null || value.toString().isBlank() ? "Non renseigné" : value.toString();
    }

    public void setIData(InterfaceIhmMainCallsDataClient dataCalls) {
        this.dataCalls = dataCalls;
    }

    public void setIComm(InterfaceIHMMainCallsComm commCalls) {
        this.commCalls = commCalls;
    }

    public void setIShare(InterfaceIHMMainCallsIHMShare shareCalls) {
        this.shareCalls = shareCalls;
    }

    public InterfaceIhmMainCallsDataClient getIData() {
        return dataCalls;
    }

    public InterfaceIHMMainCallsComm getIComm() {
        return commCalls;
    }

    public InterfaceIHMMainCallsIHMShare getIShare() {
        return shareCalls;
    }

    /** Builds the main window and displays the login screen. */
    public void start(Stage stage) {
        LoadedView view = loadView(LOGIN_VIEW);
        primaryScene = new Scene(view.root(), 1280, 800);
        primaryScene.getStylesheets().add(IHMMainManager.class.getResource(STYLESHEET).toExternalForm());
        stage.setTitle("UTube");
        stage.setScene(primaryScene);
        stage.show();
        view.controller().refreshView();
    }

    /** Replaces the displayed screen with the one given by {@code fxmlPath}. */
    public void changeView(String fxmlPath) {
        LoadedView view = loadView(fxmlPath);
        primaryScene.setRoot(view.root());
        if (view.controller() instanceof LoginController) {
            displayedVideos.clear();
            if (remoteProfileDialog != null) remoteProfileDialog.close();
        }
        main_controller = view.controller() instanceof MainController main ? main : null;
        view.controller().refreshView();
    }

    /**
     * Displays the {@code fxml_path} screen in the content area of the main view:
     * the header and the users panel stay in place. Loads the main view first if
     * it is not displayed (for example right after login).
     */
    public void showContent(String fxml_path) {
        if (main_controller == null) {
            changeView(MAIN_VIEW);
        }
        LoadedView view = loadView(fxml_path);
        main_controller.setContent(view.root());
        view.controller().refreshView();
    }

    /** Gives access to the already created controller of a screen, to communicate between screens. */
    public <C extends Controller> C getController(Class<C> type) { //for a type C that is a subtype of Controller, this method takes a Class<C> as parameter and returns a C
        return type.cast(controllers.get(type)); //cast checked at runtime by Class.cast, no unchecked warning
    }

    /** Loaded view and its controller, to call refreshView() without depending on a shared field. */
    private record LoadedView(Parent root, Controller controller) {
    }

    private LoadedView loadView(String fxmlPath) {
        URL location = IHMMainManager.class.getResource(fxmlPath);
        if (location == null) {
            throw new IllegalArgumentException("Vue introuvable : " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(location);
        loader.setControllerFactory(this::createController);
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Controller controller = loader.getController();
        controllers.put(controller.getClass(), controller); //For now, the controller of the new view replaces the previous one of the same type in controllers.
        return new LoadedView(root, controller);
    }

    /** Creates the controller declared in the FXML and gives it the manager before its initialize(). */
    private Object createController(Class<?> type) {
        try {
            Object controller = type.getDeclaredConstructor().newInstance();
            if (controller instanceof Controller c) {
                c.setViewManager(this);
            }
            return controller;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Impossible de créer le contrôleur " + type.getName(), e);
        }
    }
}
