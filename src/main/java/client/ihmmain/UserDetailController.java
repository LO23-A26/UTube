package client.ihmmain;

import common.dataclasses.ContactList;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contrôleur du profil d'un·e autre utilisateur·rice (maquette 11, séquence 5.1.5.2)
 * et du menu « Ajouter à une liste » (11a).
 */
public class UserDetailController extends Controller {

    public static final String VIEW = "/client/ihmmain/user-detail-view.fxml";

    @FXML
    private Circle avatar_circle;
    @FXML
    private Label pseudo_label;
    @FXML
    private Label identity_label;
    @FXML
    private Button add_to_list_button;
    @FXML
    private TilePane shared_videos_container;
    @FXML
    private Label empty_label;

    /** Personne consultée (profil léger, connu avant l'arrivée du profil complet). */
    private UserLight displayed_user;

    /** Profil demandé à Data : affiche ce qui est déjà connu en attendant displayRemoteProfile. */
    public void waitForProfile(UserLight user) {
        displayed_user = user;
        pseudo_label.setText(user.getPseudo());
        identity_label.setText("Chargement du profil…");
        fillAvatar(avatar_circle, user.getImage_path());
        shared_videos_container.getChildren().clear();
        empty_label.setVisible(false);
    }

    /** Affiche le profil complet reçu : avatar, pseudo, nom, prénom, âge et vidéos partagées. */
    public void renderProfile(UserFull heavy_profile) {
        displayed_user = heavy_profile.getSharedInfo();
        pseudo_label.setText(displayed_user.getPseudo());
        fillAvatar(avatar_circle, displayed_user.getImage_path());
        identity_label.setText(identity(heavy_profile.getLastName(), heavy_profile.getFirstName(),
                heavy_profile.getBirthDate(), LocalDate.now()));

        List<VideoLight> videos = heavy_profile.getSharedVideos() == null ? List.of()
                : new ArrayList<>(heavy_profile.getSharedVideos());
        shared_videos_container.getChildren().clear();
        videos.forEach(v -> shared_videos_container.getChildren().add(home.videoCard(v, false)));
        empty_label.setText(displayed_user.getPseudo() + " ne partage aucune vidéo.");
        empty_label.setVisible(videos.isEmpty());
    }

    /** Menu 11a : une case par liste ; cocher ajoute la personne, décocher la retire. Fermé au clic en dehors. */
    @FXML
    private void openAddToListMenu() {
        if (displayed_user == null || !isAvailable(data_calls, "Data")) {
            return;
        }
        List<ContactList> lists;
        try {
            lists = ContactListsController.orderLists(data_calls.getContactLists());
        } catch (RuntimeException e) {
            showError("Listes de contacts indisponibles : " + e.getMessage());
            return;
        }
        ContextMenu menu = new ContextMenu();
        for (ContactList list : lists) {
            CheckBox box = new CheckBox(list.getListName());
            box.setSelected(ContactListsController.contactsOf(list).stream()
                    .anyMatch(u -> u.getUuid().equals(displayed_user.getUuid())));
            box.setOnAction(e -> toggleContactInList(list.getListId(), box.isSelected()));
            CustomMenuItem item = new CustomMenuItem(box, false);
            menu.getItems().add(item);
        }
        menu.show(add_to_list_button, Side.BOTTOM, 0, 6);
    }

    private void toggleContactInList(UUID list_id, boolean selected) {
        try {
            if (selected) {
                data_calls.addContactToList(list_id, displayed_user.getUuid());
            } else {
                data_calls.removeContactFromList(list_id, displayed_user.getUuid());
            }
        } catch (RuntimeException e) {
            showError("Modification de la liste impossible : " + e.getMessage());
        }
    }

    @FXML
    private void back() {
        home.goBack();
    }

    /** « Nom Prénom · Âge » avec les seules informations présentes. */
    static String identity(String last_name, String first_name, LocalDate birth_date, LocalDate today) {
        String names = Stream.of(last_name, first_name)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
        String age = birth_date == null ? "" : age(birth_date, today) + " ans";
        String identity = Stream.of(names, age).filter(s -> !s.isEmpty()).collect(Collectors.joining(" · "));
        return identity.isEmpty() ? "Informations non renseignées" : identity;
    }

    /** Âge en années révolues, calculé à partir de la date de naissance. */
    static int age(LocalDate birth_date, LocalDate today) {
        return Period.between(birth_date, today).getYears();
    }
}
