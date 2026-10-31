package client.ihmmain;

import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
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
    private final InterfaceIhmMainCallsDataClient interfaceCallsData;
    private Scene primaryScene;

    public IHMMainManager(InterfaceIhmMainCallsDataClient interfaceCallsData) {
        this.interfaceCallsData = interfaceCallsData;
    }

    public InterfaceIhmMainCallsDataClient getInterfaceCallsData() {
        return interfaceCallsData;
    }

    /** Construit la fenêtre principale et affiche l'écran de connexion. */
    public void start(Stage stage) {
        primaryScene = new Scene(loadView(LOGIN_VIEW), 1280, 800);
        primaryScene.getStylesheets().add(IHMMainManager.class.getResource(STYLESHEET).toExternalForm());
        stage.setTitle("UTube");
        stage.setScene(primaryScene);
        stage.show();
    }

    /** Remplace l'écran affiché par celui désigné par {@code fxmlPath}. */
    public void changeView(String fxmlPath) {
        primaryScene.setRoot(loadView(fxmlPath));
    }

    /** Donne accès au contrôleur déjà instancié d'un écran, pour communiquer entre écrans. */
    @SuppressWarnings("unchecked") // Parce que sinon y a des warnings a cause du template
    public <C extends Controller> C getController(Class<C> type) { //pour un type C qui est un sous-type de Controller, cette méthode prend un Class<C> en paramètre et renvoie un C
        return (C) controllers.get(type); //Voir comment du controllers pour comprendre ça
    }

    private Parent loadView(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(IHMMainManager.class.getResource(fxmlPath)); //getResource est un truc générique utilisable par n'importe quel objet class, on utilise IHMMainManager par défaut
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Controller controller = loader.getController();
        controller.setViewManager(this);
        controllers.put(controller.getClass(), controller); //Pour l'instant, on va écraser l'ancienne view par la nouvelle view qu'on crée dans les controllers.
        controller.refreshView();
        return root;
    }
}
