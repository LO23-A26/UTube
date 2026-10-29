package client.ihmmain;

import common.dataclasses.UserLight;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Fenêtre d'ajout de contacts à une liste (maquette 10b, séquence 5.1.6.4).
 */
public class AddContactDialogController extends Controller {

    public static final String VIEW = "/client/ihmmain/add-contact-dialog.fxml";

    @FXML
    private Label dialog_title_label;
    @FXML
    private VBox available_users_container;
    @FXML
    private Label empty_label;

    private ContactListsController contact_lists_controller;

    /** Récupère les connecté·e·s et les contacts déjà présents, affiche les contacts disponibles. */
    public void openAddContact(ContactListsController contact_lists_controller, UUID list_id, String list_name) {
        this.contact_lists_controller = contact_lists_controller;
        dialog_title_label.setText("Ajouter un contact à « " + list_name + " »");
        if (!isAvailable(data_calls, "Data")) {
            return;
        }
        try {
            displayAvailableContacts(list_id, data_calls.getConnectedUsers(), data_calls.getContacts(list_id));
        } catch (RuntimeException e) {
            showError("Contacts indisponibles : " + e.getMessage());
            return;
        }
        showDialog();
    }

    public void displayAvailableContacts(UUID list_id, List<UserLight> users, Set<UserLight> already_in_list) {
        available_users_container.getChildren().clear();
        for (UserLight user : availableContacts(users, already_in_list, local_user)) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Button add = new Button("Ajouter");
            add.getStyleClass().add("small");
            HBox row = new HBox(avatar(17, user), new Label(user.getPseudo()), spacer, add);
            row.getStyleClass().add("list-row");
            add.setOnAction(e -> {
                selectContactToAdd(list_id, user.getUuid());
                available_users_container.getChildren().remove(row);
                empty_label.setVisible(available_users_container.getChildren().isEmpty());
            });
            available_users_container.getChildren().add(row);
        }
        empty_label.setVisible(available_users_container.getChildren().isEmpty());
    }

    public void selectContactToAdd(UUID list_id, UUID user_id) {
        try {
            data_calls.addContactToList(list_id, user_id);
        } catch (RuntimeException e) {
            showError("Ajout impossible : " + e.getMessage());
            return;
        }
        contact_lists_controller.addContactToView(list_id, user_id);
    }

    @FXML
    private void close() {
        closeDialog();
    }

    /** Connecté·e·s qui ne sont pas déjà dans la liste, sans l'utilisateur·rice local·e. */
    static List<UserLight> availableContacts(List<UserLight> users, Collection<UserLight> already_in_list,
                                             UserLight local_user) {
        Set<UUID> excluded = already_in_list == null ? new HashSet<>()
                : already_in_list.stream().map(UserLight::getUuid).collect(Collectors.toCollection(HashSet::new));
        if (local_user != null) {
            excluded.add(local_user.getUuid());
        }
        return users.stream().filter(u -> !excluded.contains(u.getUuid())).toList();
    }
}
