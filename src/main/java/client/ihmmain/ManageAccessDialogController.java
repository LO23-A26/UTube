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

    /** Cases Télécharger, Noter, Commenter de chaque liste, dans l'ordre d'affichage. */
    private final Map<String, CheckBox[]> access_rows = new LinkedHashMap<>();

    public void openVideoAccessRights(UUID video_id, String video_title) {
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
        access_rows_container.getChildren().add(new Label("Les droits actuels ne sont pas disponibles."));
        for (ContactList list : ContactListsController.orderLists(lists)) {
            if (ContactListsController.isBlockedList(list)) {
                continue;
            }
            // Aucun droit n'est inventé : l'état reste inconnu tant que Data ne l'expose pas.
            CheckBox[] boxes = {new CheckBox("Télécharger"), new CheckBox("Noter"), new CheckBox("Commenter")};
            for (CheckBox box : boxes) {
                box.setAllowIndeterminate(true);
                box.setIndeterminate(true);
                box.setDisable(true);
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
        if (isAvailable(data_calls, "Data")) {
            data_calls.setVideoAccess(list_name, video_id, granted);
        }
        // Un accès global ne permet pas de déduire les droits télécharger/noter/commenter.
    }

    public void updateAccessView(String list_name, boolean granted) {
        CheckBox[] boxes = access_rows.get(list_name);
        if (boxes != null) {
            for (CheckBox box : boxes) {
                box.setIndeterminate(true);
            }
        }
    }

    /** Attend un contrat permettant de lire et enregistrer les trois droits distincts. */
    @FXML
    private void save() {
        // TODO intégration Data : ne pas réduire arbitrairement trois permissions à un booléen.
        showError("La modification des droits n'est pas encore disponible. Aucun droit n'a été changé.");
    }

    @FXML
    private void cancel() {
        closeDialog();
    }

}
