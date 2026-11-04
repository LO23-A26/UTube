package client.main;

import client.data.IhmMainCallsDataClientImpl;
import client.ihmmain.IHMMainManager;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * JavaFX entry point of the UTube client: starts the application, builds the
 * managers of the modules and lets the {@link IHMMainManager} handle the
 * display of the FXML views.
 */
public class UTubeApplication extends Application {

    @Override
    public void start(Stage stage) {
        loadFonts();
        IHMMainManager viewManager = new IHMMainManager();
        viewManager.setIData(new IhmMainCallsDataClientImpl());
        // setIComm/setIShare still have to be called once Comm and IHM-Share provide a concrete implementation.
        viewManager.start(stage);
    }

    /** The CSS @font-face rules fail if the project path contains a space, so the fonts are loaded here. */
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
