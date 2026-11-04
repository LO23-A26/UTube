package client.ihmmain;

import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import common.interfaces.comm.InterfaceIHMMain_calls_Comm;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de vues de l'IHM Main : charge les écrans FXML, bascule l'écran
 * affiché et donne à chaque {@link Controller} accès aux interfaces vers les
 * autres modules (Data pour l'instant ; Comm et IHM-Share suivront quand leurs
 * interfaces seront fusionnées dans cette branche).
 */
public class IHMMainManager {

    public static final String LOGIN_VIEW = "/client/ihmmain/login-view.fxml";
    public static final String MAIN_VIEW = "/client/ihmmain/main-view.fxml";
    public static final String STYLESHEET = "/assets/styles/utube.css";


    /** Pour les instances de controller, on va utiliser un map qui utilisera
     * le TYPE de la classe enfant comme CLE pour récupérer l'instance. 
     * ainsi, si on veut récupérer le login controller on aura juste à utiliser 
     * l'objet récupérer via cette opération : 
     * key = LoginController.class
     * et donc, pour récupérer l'instance de LoginController, y a juste à faire :
     * controllers.get(LoginController.class)
     */
    private final Map<Class<? extends Controller>, Controller> controllers = new HashMap<>();
    private InterfaceIhmMainCallsDataClient dataCalls;
    private InterfaceIHMMain_calls_Comm commCalls;
    private InterfaceIHMMainCallsIHMShare shareCalls;
    private Scene primaryScene;

    public void setIData(InterfaceIhmMainCallsDataClient dataCalls) {
        this.dataCalls = dataCalls;
    }

    public void setIComm(InterfaceIHMMain_calls_Comm commCalls) {
        this.commCalls = commCalls;
    }

    public void setIShare(InterfaceIHMMainCallsIHMShare shareCalls) {
        this.shareCalls = shareCalls;
    }

    public InterfaceIhmMainCallsDataClient getIData() {
        return dataCalls;
    }

    public InterfaceIHMMain_calls_Comm getIComm() {
        return commCalls;
    }

    public InterfaceIHMMainCallsIHMShare getIShare() {
        return shareCalls;
    }

    /** Construit la fenêtre principale et affiche l'écran de connexion. */
    public void start(Stage stage) {
        LoadedView view = loadView(LOGIN_VIEW);
        primaryScene = new Scene(view.root(), 1280, 800);
        primaryScene.getStylesheets().add(IHMMainManager.class.getResource(STYLESHEET).toExternalForm());
        stage.setTitle("UTube");
        stage.setScene(primaryScene);
        stage.show();
        view.controller().refreshView();
    }

    /** Remplace l'écran affiché par celui désigné par {@code fxmlPath}. */
    public void changeView(String fxmlPath) {
        LoadedView view = loadView(fxmlPath);
        primaryScene.setRoot(view.root());
        view.controller().refreshView();
    }

    /** Donne accès au contrôleur déjà instancié d'un écran, pour communiquer entre écrans. */
    public <C extends Controller> C getController(Class<C> type) { //pour un type C qui est un sous-type de Controller, cette méthode prend un Class<C> en paramètre et renvoie un C
        return type.cast(controllers.get(type)); //cast vérifié à l'exécution par Class.cast, pas de warning unchecked
    }

    /** Vue chargée et son contrôleur, pour appeler refreshView() sans dépendre d'un champ partagé. */
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
        controllers.put(controller.getClass(), controller); //Pour l'instant, on va écraser l'ancienne view par la nouvelle view qu'on crée dans les controllers.
        return new LoadedView(root, controller);
    }

    /** Crée le contrôleur déclaré dans le FXML en lui donnant le manager avant son initialize(). */
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
