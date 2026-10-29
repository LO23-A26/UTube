package client.ihmmain;

import common.dataclasses.ContactList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fenêtre de gestion des accès d'une vidéo par liste de contacts (maquette 08a, séquence 5.1.6.6).
 * Ouverte depuis IHM-Share (écrans 05c et 08) via {@link HomeController#openVideoAccessRights(UUID)}.
 */
public class ManageAccessDialogController extends Controller {

    public static final String VIEW = "/client/ihmmain/manage-access-dialog.fxml";

    @FXML
    private Label video_title_label;
    @FXML
    private VBox access_rows_container;

    private UUID video_id;
    /** Cases Télécharger, Noter, Commenter de chaque liste, dans l'ordre d'affichage. */
    private final Map<String, CheckBox[]> access_rows = new LinkedHashMap<>();

    public void openVideoAccessRights(UUID video_id, String video_title) {
        this.video_id = video_id;
        video_title_label.setText(video_title);
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            displayVideoAccessRights(video_id, data_calls.getContactLists());
        } catch (RuntimeException e) {
            showError("Listes de contacts indisponibles : " + e.getMessage());
            return;
        }
        showDialog();
    }

    /** Une ligne par liste (sauf Bloqués, sans aucun droit) avec les cases Télécharger, Noter, Commenter. */
    public void displayVideoAccessRights(UUID video_id, List<ContactList> lists) {
        access_rows.clear();
        access_rows_container.getChildren().clear();
        for (ContactList list : ContactListsController.orderLists(lists)) {
            if (ContactListsController.isBlockedList(list)) {
                continue;
            }
            // ponytail: Data n'expose pas l'accès actuel d'une liste à une vidéo, tout est coché par défaut
            CheckBox[] boxes = {new CheckBox("Télécharger"), new CheckBox("Noter"), new CheckBox("Commenter")};
            for (CheckBox box : boxes) {
                box.setSelected(true);
            }
            Label name = new Label(list.getListName());
            name.setPrefWidth(166);
            HBox row = new HBox(name, boxes[0], boxes[1], boxes[2]);
            row.getStyleClass().add("list-row");
            access_rows.put(list.getListName(), boxes);
            access_rows_container.getChildren().add(row);
        }
    }

    /** Data ajoute (granted) ou retire la vidéo de la liste, puis la ligne est mise à jour. */
    public void toggleListAccess(UUID video_id, String list_name, boolean granted) {
        data_calls.setVideoAccess(list_name, video_id, granted);
        updateAccessView(list_name, granted);
    }

    public void updateAccessView(String list_name, boolean granted) {
        CheckBox[] boxes = access_rows.get(list_name);
        if (boxes != null && !granted) {
            for (CheckBox box : boxes) {
                box.setSelected(false);
            }
        }
    }

    /** « Enregistrer » : applique l'état affiché de chaque liste. setVideoAccess ne porte qu'un booléen par liste. */
    @FXML
    private void save() {
        try {
            access_rows.forEach((list_name, boxes) -> toggleListAccess(video_id, list_name,
                    isGranted(boxes[0].isSelected(), boxes[1].isSelected(), boxes[2].isSelected())));
        } catch (RuntimeException e) {
            showError("Enregistrement impossible : " + e.getMessage());
            return;
        }
        closeDialog();
    }

    @FXML
    private void cancel() {
        closeDialog();
    }

    /** Une liste garde l'accès à la vidéo tant qu'au moins un droit est coché. */
    static boolean isGranted(boolean download, boolean rate, boolean comment) {
        return download || rate || comment;
    }
}
