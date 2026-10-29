package client.ihmmain;

import common.dataclasses.ContactList;
import common.dataclasses.UserLight;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Contrôleur des listes de contacts (maquette 10, séquences 5.1.6.1 à 5.1.6.5).
 * La liste « Bloqués » n'a ni suppression ni droits : ses contacts sont « débloqués ».
 */
public class ContactListsController extends Controller {

    public static final String VIEW = "/client/ihmmain/contact-lists-view.fxml";
    public static final String FRIENDS_LIST = "Amis";
    public static final String BLOCKED_LIST = "Bloqués";

    @FXML
    private FlowPane contact_lists_container;
    @FXML
    private Label empty_label;

    private List<ContactList> contact_lists = new ArrayList<>();

    @Override
    public void refreshView() {
        if (data_calls == null) {
            displayContactLists(List.of());
            return;
        }
        try {
            displayContactLists(data_calls.getContactLists());
        } catch (RuntimeException e) {
            showError("Listes de contacts indisponibles : " + e.getMessage());
        }
    }

    /** Une carte par liste : Amis, listes personnalisées puis Bloqués (séquence 5.1.6.1). */
    public void displayContactLists(List<ContactList> lists) {
        contact_lists = orderLists(lists);
        contact_lists_container.getChildren().clear();
        contact_lists.forEach(list -> contact_lists_container.getChildren().add(listCard(list)));
        empty_label.setVisible(contact_lists.isEmpty());
    }

    // ---------- 5.1.6.2 Créer une liste ----------

    /** « Créer une liste » : ouvre la fenêtre 10a. */
    @FXML
    private void openCreateList() {
        CreateListDialogController create = dialog(contact_lists_container, CreateListDialogController.VIEW);
        create.open(this);
    }

    /** La liste créée par la fenêtre 10a est ajoutée à la vue. */
    public void addContactListToView(ContactList list) {
        List<ContactList> lists = new ArrayList<>(contact_lists);
        lists.add(list);
        displayContactLists(lists);
    }

    // ---------- 5.1.6.3 Supprimer une liste ----------

    public void selectListToDelete(UUID list_id) {
        displayDeleteConfirmation(list_id);
    }

    public void displayDeleteConfirmation(UUID list_id) {
        ConfirmationDialogController confirmation = dialog(contact_lists_container, ConfirmationDialogController.VIEW);
        boolean confirmed = confirmation.ask("Supprimer la liste ?",
                "La liste « " + nameOf(list_id) + " » sera supprimée. Les contacts ne sont pas supprimés.",
                "Supprimer");
        if (confirmed) {
            confirmListDeletion(list_id);
        }
    }

    public void confirmListDeletion(UUID list_id) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            data_calls.deleteContactList(list_id);
        } catch (RuntimeException e) {
            showError("Suppression impossible : " + e.getMessage());
            return;
        }
        removeContactListFromView(list_id);
    }

    public void removeContactListFromView(UUID list_id) {
        displayContactLists(contact_lists.stream().filter(l -> !list_id.equals(l.getListId())).toList());
        if (home != null) {
            home.refreshView();
        }
    }

    // ---------- 5.1.6.4 Ajouter un contact ----------

    /** « Ajouter un contact » : ouvre la fenêtre 10b pour la liste choisie. */
    public void openAddContact(UUID list_id) {
        AddContactDialogController add = dialog(contact_lists_container, AddContactDialogController.VIEW);
        add.openAddContact(this, list_id, nameOf(list_id));
    }

    /** Le contact ajouté par la fenêtre 10b apparaît dans sa liste. */
    public void addContactToView(UUID list_id, UUID user_id) {
        refreshAll();
    }

    // ---------- 5.1.6.5 Retirer un contact ----------

    public void selectContactToRemove(UUID list_id, UUID user_id) {
        displayRemovalConfirmation(list_id, user_id);
    }

    public void displayRemovalConfirmation(UUID list_id, UUID user_id) {
        ConfirmationDialogController confirmation = dialog(contact_lists_container, ConfirmationDialogController.VIEW);
        boolean confirmed = confirmation.ask("Retirer ce contact ?",
                "« " + pseudoOf(list_id, user_id) + " » sera retiré·e de la liste « " + nameOf(list_id) + " ».",
                "Retirer");
        if (confirmed) {
            confirmContactRemoval(list_id, user_id);
        }
    }

    public void confirmContactRemoval(UUID list_id, UUID user_id) {
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            data_calls.removeContactFromList(list_id, user_id);
        } catch (RuntimeException e) {
            showError("Retrait impossible : " + e.getMessage());
            return;
        }
        removeContactFromView(list_id, user_id);
    }

    public void removeContactFromView(UUID list_id, UUID user_id) {
        refreshAll();
    }

    /** « Débloquer » (liste Bloqués) : retrait direct, sans confirmation (maquette 10). */
    private void unblockContact(UUID list_id, UUID user_id) {
        confirmContactRemoval(list_id, user_id);
    }

    // ---------- Affichage ----------

    private VBox listCard(ContactList list) {
        UUID list_id = list.getListId();
        boolean blocked = isBlockedList(list);

        Label name = new Label(list.getListName());
        name.getStyleClass().add("section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button add = smallButton("Ajouter un contact");
        add.setOnAction(e -> openAddContact(list_id));
        HBox head = new HBox(8, name, spacer, add);
        if (!blocked) {
            Button delete = smallButton("Supprimer la liste");
            delete.setOnAction(e -> selectListToDelete(list_id));
            head.getChildren().add(delete);
        }
        head.setAlignment(Pos.CENTER_LEFT);

        VBox card = new VBox(8, head);
        Set<UserLight> contacts = contactsOf(list);
        if (contacts.isEmpty()) {
            Label empty = new Label("Aucun contact dans cette liste.");
            empty.getStyleClass().add("text-secondary");
            card.getChildren().add(empty);
        }
        for (UserLight contact : contacts) {
            Hyperlink pseudo = new Hyperlink(contact.getPseudo());
            pseudo.setStyle("-fx-underline: false;");
            pseudo.setOnAction(e -> home.checkProfile(contact));
            Region row_spacer = new Region();
            HBox.setHgrow(row_spacer, Priority.ALWAYS);
            Button remove = smallButton(blocked ? "Débloquer" : "Retirer");
            remove.setOnAction(e -> {
                if (blocked) {
                    unblockContact(list_id, contact.getUuid());
                } else {
                    selectContactToRemove(list_id, contact.getUuid());
                }
            });
            HBox row = new HBox(avatar(13, contact), pseudo, row_spacer, remove);
            row.getStyleClass().add("list-row");
            card.getChildren().add(row);
        }
        card.getStyleClass().addAll("card", "large");
        card.setStyle("-fx-padding: 16px;");
        card.setPrefWidth(462);
        return card;
    }

    /** Rafraîchit la vue et le panneau utilisateur·rice·s, regroupé par listes. */
    private void refreshAll() {
        if (home != null) {
            home.refreshView();
        } else {
            refreshView();
        }
    }

    private String nameOf(UUID list_id) {
        return contact_lists.stream().filter(l -> list_id.equals(l.getListId()))
                .map(ContactList::getListName).findFirst().orElse("");
    }

    private String pseudoOf(UUID list_id, UUID user_id) {
        return contact_lists.stream().filter(l -> list_id.equals(l.getListId()))
                .flatMap(l -> contactsOf(l).stream())
                .filter(u -> u.getUuid().equals(user_id))
                .map(UserLight::getPseudo).findFirst().orElse("");
    }

    private static Button smallButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("small");
        return button;
    }

    /** Ordre d'affichage des listes : Amis, listes personnalisées (ordre reçu), Bloqués. */
    static List<ContactList> orderLists(List<ContactList> lists) {
        if (lists == null) {
            return List.of();
        }
        return lists.stream()
                .sorted(Comparator.comparingInt(ContactListsController::rank))
                .toList();
    }

    private static int rank(ContactList list) {
        if (FRIENDS_LIST.equals(list.getListName())) {
            return 0;
        }
        return isBlockedList(list) ? 2 : 1;
    }

    static boolean isBlockedList(ContactList list) {
        return BLOCKED_LIST.equals(list.getListName());
    }

    /** Contacts d'une liste, jamais null (ContactList(name) laisse contacts à null). */
    static Set<UserLight> contactsOf(ContactList list) {
        return list.getContacts() == null ? Set.of() : list.getContacts();
    }
}
