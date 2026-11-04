package client.main;

import client.data.IhmMainCallsDataClientImpl;
import client.ihmmain.IHMMainManager;
import client.ihmshare.IHMShareCore;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Point d'entrée JavaFX du client UTube : démarre l'application, construit les
 * managers des différents modules et laisse l'{@link IHMMainManager} gérer
 * l'affichage des vues FXML.
 */
public class UTubeApplication extends Application {

    @Override
    public void start(Stage stage) {
        loadFonts();
        IHMMainManager view_manager = new IHMMainManager(new IhmMainCallsDataClientImpl());

        // IHM-Share displays its screens through the same view manager
        IHMShareCore ihm_share_core = new IHMShareCore();
        ihm_share_core.setViewManager(view_manager);
        view_manager.setInterfaceCallsShare(ihm_share_core.getInterfaceForIHMMain());
        // TODO: ihm_share_core.setInterfaceFromData(...) / setInterfaceFromCommunication(...) once implemented

        view_manager.start(stage);
    }

    /** Les @font-face du CSS échouent si le chemin du projet contient un espace : on charge les polices ici. */
    private static void loadFonts() {
        for (String font : new String[]{"Regular", "Italic", "Medium", "SemiBold"}) {
            try (InputStream in = UTubeApplication.class.getResourceAsStream("/assets/fonts/IBMPlexSans-" + font + ".ttf")) {
                Font.loadFont(in, 14);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
