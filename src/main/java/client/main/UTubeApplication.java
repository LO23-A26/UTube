package client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Point d'entrée JavaFX du client UTube.
 */
public class UTubeApplication extends Application {

    public static final String MAIN_VIEW = "/client/ihmmain/main-view.fxml";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(UTubeApplication.class.getResource(MAIN_VIEW));
        Scene scene = new Scene(loader.load(), 960, 600);
        stage.setTitle("UTube");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
