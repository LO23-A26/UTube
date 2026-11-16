package client.main;

import client.ihmmain.IHMMainManager;
import client.communication.CommClientController;
import client.data.CommClientCallsDataClientImpl;
import client.data.DataClientCore;
import client.data.IhmMainCallsDataClientImpl;
import client.data.ProfileSaver;
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

    // Preserve the resource constants used by develop's existing tests.
    public static final String MAIN_VIEW = IHMMainManager.MAIN_VIEW;
    public static final String LOGIN_VIEW = IHMMainManager.LOGIN_VIEW;
    public static final String STYLESHEET = IHMMainManager.STYLESHEET;

    @Override
    public void start(Stage stage) {
        loadFonts();
        createManager().start(stage);
    }

    /** Wires existing client services through their public APIs; does not start a connection. */
    public static IHMMainManager createManager() {
        IHMMainManager manager = new IHMMainManager();
        DataClientCore data = new DataClientCore();
        data.setProfileSaver(new ProfileSaver());
        IhmMainCallsDataClientImpl dataCalls = new IhmMainCallsDataClientImpl();
        dataCalls.setDataClientCore(data);
        data.setImplInterfaceForICM(dataCalls);

        CommClientController comm = new CommClientController();
        data.setInterfaceFromComm(comm.getDataCallsComm());
        comm.setInterfaceCommDataClient(new CommClientCallsDataClientImpl());
        manager.setIData(dataCalls);
        manager.setIComm(comm.getIhmMainCallsComm());
        return manager;
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
