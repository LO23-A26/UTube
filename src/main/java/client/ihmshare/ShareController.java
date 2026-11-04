package client.ihmshare;

import client.ihmmain.Controller;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Common base of the IHM-Share controllers.
 * <p>
 * The views are loaded by the shared view manager (so the controllers extend {@link Controller}),
 * then {@link IHMShareCore} gives them a pointer to itself with {@link #setCore}.
 *
 * @author IHM-Share
 * @version 1.0
 */
public abstract class ShareController extends Controller {

    protected IHMShareCore core;

    /**
     * Keep a pointer to the IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the core of the module
     */
    public void setCore(IHMShareCore core) {
        this.core = core;
    }

    /**
     * Give the core to a controller freshly loaded by the view manager (JavaFX creates the
     * controllers, so they cannot receive it in their constructor), then refresh it
     *
     * @param controller (C) controller returned by the view manager
     * @param core       (IHMShareCore) core of the module
     * @return (C) the same controller, ready to receive its data
     */
    public static <C extends ShareController> C attach(C controller, IHMShareCore core) {
        controller.setCore(core);
        controller.refreshView();
        return controller;
    }

    /**
     * Show an error message to the user
     *
     * @param message (String) message to display
     */
    protected void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /**
     * Clickable video row of the screens 07 and 08: title, subtitle, buttons, chevron
     *
     * @param title    (String) title of the video
     * @param subtitle (String) second line (uploader, or tags and creator)
     * @param on_click (Runnable) action when the row is clicked (opens the video detail)
     * @param buttons  (Button...) action buttons of the row
     * @return (HBox) the row, styled as a clickable card
     */
    protected static HBox videoRow(String title, String subtitle, Runnable on_click, Button... buttons) {
        Label title_label = new Label(title);
        title_label.getStyleClass().add("field-label");
        title_label.setStyle("-fx-font-size: 15px;");
        Label subtitle_label = new Label(subtitle);
        subtitle_label.getStyleClass().add("text-secondary");
        VBox texts = new VBox(4, title_label, subtitle_label);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(12, texts, spacer);
        for (Button button : buttons) {
            button.getStyleClass().add("small");
            row.getChildren().add(button);
        }
        ImageView chevron = new ImageView(new Image(ShareController.class.getResource("/assets/icons/chevron.png").toExternalForm()));
        row.getChildren().add(chevron);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().addAll("card", "clickable");
        row.setStyle("-fx-padding: 22px 16px;");
        row.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof Node target) || !isInsideButton(target)) {
                on_click.run();
            }
        });
        return row;
    }

    private static boolean isInsideButton(Node node) {
        for (Node current = node; current != null; current = current.getParent()) {
            if (current instanceof Button) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ask a confirmation before a deletion (fig. 8.10)
     *
     * @param title        (String) question displayed as title
     * @param message      (String) consequence of the action
     * @param action_label (String) label of the action button ("Supprimer")
     * @return (boolean) true if the user confirmed
     */
    protected boolean confirm(String title, String message, String action_label) {
        ButtonType action = new ButtonType(action_label, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.NONE, message, cancel, action);
        alert.setTitle(title);
        alert.setHeaderText(title);
        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == action;
    }
}
