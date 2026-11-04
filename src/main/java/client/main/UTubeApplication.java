package client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Point d'entrée JavaFX du client UTube.
 */
public class UTubeApplication extends Application {

    public static final String MAIN_VIEW = "/client/ihmmain/main-view.fxml";
    public static final String LOGIN_VIEW = "/client/ihmmain/login-view.fxml";
    public static final String STYLESHEET = "/assets/styles/utube.css";

    @Override
    public void start(Stage stage) {
        loadFonts();
        Scene scene = new Scene(load(LOGIN_VIEW), 1280, 800);
        scene.getStylesheets().add(UTubeApplication.class.getResource(STYLESHEET).toExternalForm());
        stage.setTitle("UTube");
        stage.setScene(scene);
        stage.show();
    }

    /** Remplace le contenu de la fenêtre par la vue FXML donnée. */
    public static void show(Scene scene, String view) {
        scene.setRoot(load(view));
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

    private static Parent load(String view) {
        try {
            return FXMLLoader.load(UTubeApplication.class.getResource(view));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
