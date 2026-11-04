package client.ihmmain;

import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * View manager (ViewManager, fig. 6.4), shared by IHM-Main and IHM-Share.
 * <p>
 * Two display levels:
 * <ul>
 *   <li>{@link #changeView} replaces the whole window: offline screens (01, 02, 03) and the
 *       connected layout {@link #MAIN_VIEW} (header + central area);</li>
 *   <li>{@link #changeContent} and {@link #openTab} only replace the central area of the
 *       connected layout, so the header and the users panel stay visible, as required by the
 *       design document for both IHM-Main and IHM-Share screens.</li>
 * </ul>
 * The "Retour" button relies on the history of the central area ({@link #goBack()}, {@link #backTo}).
 * IHM-Share views are loaded by this same manager: their controllers also extend {@link Controller}.
 */
public class IHMMainManager {

    public static final String LOGIN_VIEW = "/client/ihmmain/login-view.fxml";
    public static final String MAIN_VIEW = "/client/ihmmain/main-view.fxml";
    public static final String STYLESHEET = "/assets/styles/utube.css";

    /**
     * A view displayed in the central area, kept as is so that going back restores its state.
     * A null root stands for the home content of the layout.
     */
    private record ContentEntry(String fxml_path, Parent root, Controller controller) {
    }

    /**
     * Controller instances, indexed by their class: the login controller is retrieved with
     * {@code controllers.get(LoginController.class)}.
     */
    private final Map<Class<? extends Controller>, Controller> controllers = new HashMap<>();
    private final InterfaceIhmMainCallsDataClient data_calls;
    private InterfaceIHMMainCallsIHMShare share_calls;
    private Scene primary_scene;

    /** Central content shown when the connected layout is opened (home screen 04). */
    private Node home_content;
    private ContentEntry current_content;
    /** Previous central views, most recent first. */
    private final Deque<ContentEntry> history = new ArrayDeque<>();

    public IHMMainManager(InterfaceIhmMainCallsDataClient data_calls) {
        this.data_calls = data_calls;
    }

    public InterfaceIhmMainCallsDataClient getInterfaceCallsData() {
        return data_calls;
    }

    public InterfaceIHMMainCallsIHMShare getInterfaceCallsShare() {
        return share_calls;
    }

    public void setInterfaceCallsShare(InterfaceIHMMainCallsIHMShare share_calls) {
        this.share_calls = share_calls;
    }

    /** Builds the main window and displays the login screen. */
    public void start(Stage stage) {
        primary_scene = new Scene(loadView(LOGIN_VIEW), 1280, 800);
        primary_scene.getStylesheets().add(IHMMainManager.class.getResource(STYLESHEET).toExternalForm());
        stage.setTitle("UTube");
        stage.setScene(primary_scene);
        stage.show();
    }

    /** Replaces the whole window with the view {@code fxml_path}; the history is cleared. */
    public void changeView(String fxml_path) {
        Parent root = loadView(fxml_path);
        history.clear();
        current_content = null;
        home_content = root instanceof BorderPane layout ? layout.getCenter() : null;
        primary_scene.setRoot(root);
    }

    /**
     * Displays a view in the central area of the connected layout and keeps the current one
     * in the history for the "Retour" button.
     *
     * @param fxml_path path of the FXML file of the view
     * @return the controller of the displayed view, to give it its data
     */
    public <C extends Controller> C changeContent(String fxml_path) {
        history.push(current_content != null ? current_content : new ContentEntry(null, null, null));
        return showContent(fxml_path);
    }

    /**
     * Header tab (Téléchargements, Partages, Amis): displays the view in the central area
     * and clears the history, these screens having no "Retour" button.
     *
     * @param fxml_path path of the FXML file of the view
     * @return the controller of the displayed view
     */
    public <C extends Controller> C openTab(String fxml_path) {
        history.clear();
        return showContent(fxml_path);
    }

    /** "Accueil" tab: displays the original content of the connected layout again. */
    public void showHome() {
        history.clear();
        current_content = null;
        layout().setCenter(home_content);
    }

    /** "Retour" button: displays the previous view again (or the home screen if there is none). */
    public void goBack() {
        if (history.isEmpty()) {
            showHome();
        } else {
            restore(history.pop());
        }
    }

    /**
     * Goes back to a given view (e.g. 09 Ajouter un partage → 08 Mes partages).
     * If the view is in the history, it is restored with its state; otherwise it is loaded
     * again as a tab.
     *
     * @param fxml_path path of the FXML file of the view
     * @return the controller of the displayed view
     */
    public <C extends Controller> C backTo(String fxml_path) {
        boolean in_history = history.stream().anyMatch(entry -> fxml_path.equals(entry.fxml_path()));
        if (!in_history) {
            return openTab(fxml_path);
        }
        ContentEntry target = history.pop();
        while (!fxml_path.equals(target.fxml_path())) {
            target = history.pop();
        }
        restore(target);
        return cast(target.controller());
    }

    /** Gives access to the controller of an already loaded view, to communicate between views. */
    @SuppressWarnings("unchecked") // the map only stores instances of their own key class
    public <C extends Controller> C getController(Class<C> type) {
        return (C) controllers.get(type);
    }

    private <C extends Controller> C showContent(String fxml_path) {
        FXMLLoader loader = loader(fxml_path);
        Parent root = load(loader);
        Controller controller = register(loader);
        current_content = new ContentEntry(fxml_path, root, controller);
        layout().setCenter(root);
        controller.refreshView();
        return cast(controller);
    }

    private void restore(ContentEntry entry) {
        if (entry.root() == null) {
            showHome();
            return;
        }
        current_content = entry;
        layout().setCenter(entry.root());
        entry.controller().refreshView();
    }

    private BorderPane layout() {
        if (!(primary_scene.getRoot() instanceof BorderPane layout)) {
            throw new IllegalStateException("The central area only exists in the connected layout " + MAIN_VIEW);
        }
        return layout;
    }

    private Parent loadView(String fxml_path) {
        FXMLLoader loader = loader(fxml_path);
        Parent root = load(loader);
        register(loader).refreshView();
        return root;
    }

    private static FXMLLoader loader(String fxml_path) {
        return new FXMLLoader(IHMMainManager.class.getResource(fxml_path));
    }

    private static Parent load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Gives the manager to the new controller and keeps it (the previous instance is replaced). */
    private Controller register(FXMLLoader loader) {
        Controller controller = loader.getController();
        controller.setViewManager(this);
        controllers.put(controller.getClass(), controller);
        return controller;
    }

    @SuppressWarnings("unchecked")
    private static <C extends Controller> C cast(Controller controller) {
        return (C) controller;
    }
}
