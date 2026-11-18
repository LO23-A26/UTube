package client.ihmmain;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
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
    private InterfaceIhmMainCallsDataClient data_calls;
    private InterfaceIHMMainCallsComm comm_calls;
    private InterfaceIHMMainCallsIHMShare share_calls;
    private Scene primary_scene;
    /** Controller of the main view (header and users panel) while it is displayed, null otherwise. */
    private MainController main_controller;

    private final InterfaceDataCallsIHMMain data_notifications = new DataCallsIHMMainImpl(this);
    private final InterfaceCommCallsIHMMain comm_notifications = new CommCallsIHMMainImpl(this);
    // UI state only. Data and COM retain responsibility for storage and network indexes.
    // Both maps are filled by the notifications of Data/COM and only touched on the JavaFX thread.
    private final Map<UUID, UserLight> displayed_users = new LinkedHashMap<>();
    private final Map<UUID, VideoLight> displayed_videos = new LinkedHashMap<>();
    private Alert remote_profile_dialog;

    public InterfaceDataCallsIHMMain getInterfaceForData() { return data_notifications; }
    public InterfaceCommCallsIHMMain getInterfaceForComm() { return comm_notifications; }

    List<UserLight> getDisplayedUsers() { return List.copyOf(displayed_users.values()); }

    List<VideoLight> getDisplayedVideos() { return List.copyOf(displayed_videos.values()); }

    void addUsers(Collection<UserLight> users) {
        for (UserLight user : users) {
            if (user != null && user.getUuid() != null) displayed_users.put(user.getUuid(), user);
        }
        refreshUsers();
    }

    void removeUser(UUID id) {
        displayed_users.remove(id);
        refreshUsers();
    }

    /** Redraws the users panel of the main view, if it is displayed. */
    void refreshUsers() {
        if (main_controller != null) main_controller.refreshView();
    }

    void addVideos(Collection<VideoLight> videos) {
        for (VideoLight video : videos) {
            if (video != null && video.getVideoId() != null) displayed_videos.put(video.getVideoId(), video);
        }
        refreshVideos();
    }

    void replaceVideos(Collection<VideoLight> videos) {
        displayed_videos.clear();
        addVideos(videos);
    }

    void removeVideo(UUID id) {
        displayed_videos.remove(id);
        refreshVideos();
    }

    void removeUserVideos(UUID userId) {
        displayed_videos.values().removeIf(video -> video.getOwner() != null
                && userId.equals(video.getOwner().getUuid()));
        refreshVideos();
    }

    private void refreshVideos() {
        HomeController home = getController(HomeController.class);
        if (main_controller != null && home != null) home.refreshView();
    }

    /** Displays only the received profile fields, never credentials or local collections. */
    void displayRemoteProfile(UserFull profile) {
        if (remote_profile_dialog == null) {
            remote_profile_dialog = new Alert(Alert.AlertType.INFORMATION);
            remote_profile_dialog.setTitle("Profil utilisateur");
            if (primary_scene != null && primary_scene.getWindow() != null) {
                remote_profile_dialog.initOwner(primary_scene.getWindow());
            }
        }
        if (profile == null || profile.getSharedInfo() == null) {
            remote_profile_dialog.setHeaderText("Profil indisponible");
            remote_profile_dialog.setContentText("Le profil demandé n'a pas été fourni.");
        } else {
            remote_profile_dialog.setHeaderText(profile.getSharedInfo().getPseudo());
            remote_profile_dialog.setContentText("Prénom : " + profileField(profile.getFirstName())
                    + "\nNom : " + profileField(profile.getLastName())
                    + "\nDate de naissance : " + profileField(profile.getBirthDate()));
        }
        remote_profile_dialog.show();
    }

    private static String profileField(Object value) {
        return value == null || value.toString().isBlank() ? "Non renseigné" : value.toString();
    }

    public void setIData(InterfaceIhmMainCallsDataClient data_calls) {
        this.data_calls = data_calls;
    }

    public void setIComm(InterfaceIHMMainCallsComm comm_calls) {
        this.comm_calls = comm_calls;
    }

    public void setIShare(InterfaceIHMMainCallsIHMShare share_calls) {
        this.share_calls = share_calls;
    }

    public InterfaceIhmMainCallsDataClient getIData() {
        return data_calls;
    }

    public InterfaceIHMMainCallsComm getIComm() {
        return comm_calls;
    }

    public InterfaceIHMMainCallsIHMShare getIShare() {
        return share_calls;
    }

    /** Builds the main window and displays the login screen. */
    public void start(Stage stage) {
        LoadedView view = loadView(LOGIN_VIEW);
        primary_scene = new Scene(view.root(), 1280, 800);
        primary_scene.getStylesheets().add(IHMMainManager.class.getResource(STYLESHEET).toExternalForm());
        stage.setTitle("UTube");
        stage.setScene(primary_scene);
        stage.show();
        view.controller().refreshView();
    }

    /** Replaces the displayed screen with the one given by {@code fxmlPath}. */
    public void changeView(String fxmlPath) {
        LoadedView view = loadView(fxmlPath);
        primary_scene.setRoot(view.root());
        if (view.controller() instanceof LoginController) {
            displayed_users.clear();
            displayed_videos.clear();
            if (remote_profile_dialog != null) remote_profile_dialog.close();
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
