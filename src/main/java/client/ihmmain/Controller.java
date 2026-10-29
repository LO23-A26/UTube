package client.ihmmain;

import common.dataclasses.UserLight;
import common.interfaces.data.InterfaceIhmMainCallsDataClient;
import common.interfaces.comm.InterfaceIHMMain_calls_Comm;
import common.interfaces.IHMShare.InterfaceIHMMainCallsIHMShare;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Classe abstraite des contrôleurs IHM Main (fig. 6.4).
 * Les interfaces des autres modules sont injectées par setters et transmises d'une vue à l'autre.
 */
public abstract class Controller {

    // TODO: #ViewManager view_manager de la fig. 6.4, ajouté avec le ViewManager
    protected InterfaceIhmMainCallsDataClient data_calls;

    protected InterfaceIHMMain_calls_Comm comm_calls;
    protected InterfaceIHMMainCallsIHMShare share_calls;

    protected UserLight local_user;
    protected String server_ip;
    protected int server_port;
    /** Layout connecté (header + panneau) qui affiche cette vue, null hors connexion. */
    protected HomeController home;

    private String current_view;
    private String previous_view;
    private Stage dialog_stage;

    public void setDataCalls(InterfaceIhmMainCallsDataClient data_calls) {
        this.data_calls = data_calls;
    }

    public void setCommCalls(InterfaceIHMMain_calls_Comm comm_calls) {
        this.comm_calls = comm_calls;
    }

    public void setShareCalls(InterfaceIHMMainCallsIHMShare share_calls) {
        this.share_calls = share_calls;
    }

    public void setLocalUser(UserLight local_user) {
        this.local_user = local_user;
    }

    public void setServer(String server_ip, int server_port) {
        this.server_ip = server_ip;
        this.server_port = server_port;
    }

    /** Recharge l'affichage depuis les interfaces ; appelé après chaque ouverture de vue. */
    public void refreshView() {
    }

    /** Remplace tout le contenu de la fenêtre par la vue donnée (écrans 01, 02, 03 et layout 04). */
    protected <T extends Controller> T navigate(Node anchor, String view) {
        FXMLLoader loader = loader(view);
        anchor.getScene().setRoot(load(loader));
        T next = loader.getController();
        Controller opened = next;
        opened.previous_view = current_view;
        opened.current_view = view;
        copyTo(opened);
        opened.refreshView();
        return next;
    }

    /** Retour à l'écran plein précédent, ou à l'écran par défaut. */
    protected void goBack(Node anchor, String default_view) {
        navigate(anchor, previous_view != null ? previous_view : default_view);
    }

    /** Transmet interfaces et session au contrôleur d'une autre vue. */
    protected void copyTo(Controller next) {
        next.setDataCalls(data_calls);
        next.setCommCalls(comm_calls);
        next.setShareCalls(share_calls);
        next.setLocalUser(local_user);
        next.setServer(server_ip, server_port);
        next.home = next instanceof HomeController layout ? layout : home;
    }

    /** Charge une fenêtre modale (voile noir à 45 % sur l'écran d'origine) sans l'afficher. */
    protected <T extends Controller> T dialog(Node anchor, String view) {
        FXMLLoader loader = loader(view);
        Parent content = load(loader);
        StackPane veil = new StackPane(content);
        veil.getStyleClass().add("modal-overlay");

        Scene origin = anchor.getScene();
        Window owner = origin.getWindow();
        Scene scene = new Scene(veil, origin.getWidth(), origin.getHeight(), Color.TRANSPARENT);
        scene.getStylesheets().addAll(origin.getStylesheets());
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setX(owner.getX() + origin.getX());
        stage.setY(owner.getY() + origin.getY());

        T controller = loader.getController();
        ((Controller) controller).dialog_stage = stage;
        copyTo(controller);
        return controller;
    }

    /** Affiche la fenêtre modale chargée par {@link #dialog} et attend sa fermeture. */
    protected void showDialog() {
        dialog_stage.showAndWait();
    }

    protected void closeDialog() {
        dialog_stage.close();
    }

    /** Vérifie qu'un module est branché ; sinon prévient l'utilisateur·rice au lieu de planter. */
    protected boolean isAvailable(Object calls, String module) {
        if (calls == null) {
            showError("Le module " + module + " n'est pas disponible.");
        }
        return calls != null;
    }

    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /** Remplit un avatar rond avec l'image donnée, ou le laisse gris par défaut. */
    protected static void fillAvatar(Circle avatar, String image_path) {
        avatar.setStroke(Color.web("#D9DBE0"));
        if (image_path != null && new File(image_path).isFile()) {
            avatar.setFill(new ImagePattern(new Image(new File(image_path).toURI().toString())));
        } else {
            avatar.setFill(Color.web("#E6E7E3"));
        }
    }

    /** Avatar rond d'une ligne de liste (panneau, listes de contacts, fenêtres). */
    protected static Circle avatar(double radius, UserLight user) {
        Circle avatar = new Circle(radius);
        fillAvatar(avatar, user == null ? null : user.getImage_path());
        return avatar;
    }

    static FXMLLoader loader(String view) {
        return new FXMLLoader(Controller.class.getResource(view));
    }

    static Parent load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
