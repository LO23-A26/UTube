package client.ihmmain;

import common.dataclasses.AccessRight;
import common.dataclasses.ContactList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Fenêtre de création d'une liste de contacts (maquette 10a, séquence 5.1.6.2).
 */
public class CreateListDialogController extends Controller {

    public static final String VIEW = "/client/ihmmain/create-list-dialog.fxml";

    @FXML
    private TextField list_name_field;
    @FXML
    private CheckBox download_checkbox;
    @FXML
    private CheckBox rate_checkbox;
    @FXML
    private CheckBox comment_checkbox;
    @FXML
    private Label error_label;

    private ContactListsController contact_lists_controller;

    /** Ouvre la fenêtre ; la liste créée est ajoutée à l'écran 10 appelant. */
    public void open(ContactListsController contact_lists_controller) {
        this.contact_lists_controller = contact_lists_controller;
        showDialog();
    }

    @FXML
    private void createList() {
        String name = list_name_field.getText().strip();
        if (name.isEmpty()) {
            displayError("Le nom de la liste est obligatoire.");
            return;
        }
        AccessRight access_right = toAccessRight(download_checkbox.isSelected(), rate_checkbox.isSelected(),
                comment_checkbox.isSelected());
        if (access_right == null) {
            displayError("Combinaison non prise en charge : tout cocher, seulement « Télécharger », ou rien.");
            return;
        }
        submitNewList(name, access_right);
    }

    /** Data crée la liste ; si le nom existe déjà, un message d'erreur s'affiche dans la fenêtre. */
    public void submitNewList(String name, AccessRight access_right) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        ContactList list;
        try {
            list = data_calls.createContactList(name, access_right);
        } catch (RuntimeException e) {
            // Le contrat actuel ne permet pas d'identifier précisément un doublon.
            displayError("Création impossible : " + e.getMessage());
            return;
        }
        if (list == null) {
            displayError("La création de la liste n'a pas été confirmée.");
            return;
        }
        contact_lists_controller.addContactListToView(list);
        closeDialog();
    }

    public void displayError(String message) {
        error_label.setText(message);
        error_label.setVisible(true);
    }

    @FXML
    private void cancel() {
        closeDialog();
    }

    /**
     * Cases Télécharger / Noter / Commenter vers l'AccessRight de Data :
     * tout coché = all, Télécharger seul = download_and_details_only, rien = none, sinon null (non représentable).
     */
    static AccessRight toAccessRight(boolean download, boolean rate, boolean comment) {
        if (download && rate && comment) {
            return AccessRight.all;
        }
        if (download && !rate && !comment) {
            return AccessRight.download_and_details_only;
        }
        if (!download && !rate && !comment) {
            return AccessRight.none;
        }
        return null;
    }
}
