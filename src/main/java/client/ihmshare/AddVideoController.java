package client.ihmshare;

import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.data.InterfaceIhmShareCallsDataClient;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller of the "Ajouter un partage" screen (09), sequence "Ajout vidéo".
 *
 * @author IHM-Share
 * @version 3.0
 */
public class AddVideoController extends ShareController {

    public static final String VIEW = "/client/ihmshare/add-video-view.fxml";

    /** Accepted video formats (§8.3). */
    static final List<String> ACCEPTED_EXTENSIONS = List.of("mp4", "avi");

    @FXML
    private Label file_label;
    @FXML
    private TextField title_field;
    @FXML
    private TextField creator_field;
    @FXML
    private TextField tags_field;
    @FXML
    private TextArea description_field;
    @FXML
    private VBox rights_box;
    @FXML
    private Label error_label;

    private Path chosen_file;

    /**
     * Fill the form with a downloaded video to share again ("Re-partager" from 07)
     *
     * @param source (VideoFull) downloaded video, or null for a new video
     */
    public void prefill(VideoFull source) {
        if (source == null) {
            return;
        }
        if (source.getFilePath() != null) {
            setChosenFile(Path.of(source.getFilePath()));
        }
        VideoLight info = source.getSharedInfo();
        if (info != null) {
            title_field.setText(info.getTitle());
            creator_field.setText(info.getCreator());
            if (info.getTags() != null) {
                tags_field.setText(String.join(", ", info.getTags()));
            }
        }
    }

    @Override
    public void refreshView() {
        error_label.setText("");
        if (rights_box.getChildren().isEmpty()) {
            // TODO: one row per contact list of the user, once Data gives the lists to IHM-Share.
            // The rights are not sent yet: addVideo of Data has no parameter for them.
            rights_box.getChildren().add(rightsRow("Amis"));
        }
    }

    /** "Choisir un fichier vidéo": system file chooser restricted to MP4 and AVI. */
    @FXML
    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un fichier vidéo");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Vidéos (MP4, AVI)", "*.mp4", "*.avi"));
        File file = chooser.showOpenDialog(file_label.getScene().getWindow());
        if (file != null) {
            setChosenFile(file.toPath());
        }
    }

    /**
     * Check the format of the chosen file (fig. 6.5: checkFormat)
     *
     * @param file (Path) path of the video to add
     * @return (boolean) true if the file exists and is an MP4 or AVI video
     */
    static boolean checkFormat(Path file) {
        if (file == null || !Files.isRegularFile(file)) {
            return false;
        }
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        int dot = name.lastIndexOf('.');
        return dot >= 0 && ACCEPTED_EXTENSIONS.contains(name.substring(dot + 1));
    }

    /**
     * "Partager": check the form then give the video to Data (fig. 6.5: checkVideoForm)
     */
    @FXML
    public void checkVideoForm() {
        String error = validate(chosen_file, title_field.getText());
        if (error != null) {
            error_label.setText(error);
            return;
        }
        InterfaceIhmShareCallsDataClient data = core.getInterfaceFromData();
        if (data == null) {
            error_label.setText("Le partage n'est pas disponible pour le moment.");
            return;
        }
        // TODO: the description and the access rights are not sent, addVideo has no parameter for them
        data.addVideo(chosen_file, title_field.getText().strip(), creator_field.getText().strip(), parseTags(tags_field.getText()));
        backToMyShares();
    }

    /** "Annuler" and "Retour": back to "Mes partages" (08). */
    @FXML
    private void cancel() {
        backToMyShares();
    }

    /** Back to "Mes partages" (08), restored from the history if it was the previous screen. */
    private void backToMyShares() {
        attach(view_manager.backTo(ManageVideosController.VIEW), core);
    }

    /**
     * Check the form of the screen 09
     *
     * @return (String) the error message to display, or null if the form is valid
     */
    static String validate(Path file, String title) {
        if (file == null) {
            return "Choisissez un fichier vidéo.";
        }
        if (!checkFormat(file)) {
            return "Le fichier doit être une vidéo MP4 ou AVI.";
        }
        if (title == null || title.isBlank()) {
            return "Le titre est obligatoire.";
        }
        return null;
    }

    /** "a, b ,,c" → {a, b, c} */
    static Set<String> parseTags(String text) {
        if (text == null) {
            return new LinkedHashSet<>();
        }
        return Arrays.stream(text.split(","))
                .map(String::strip)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private void setChosenFile(Path file) {
        chosen_file = file;
        file_label.setText(file.getFileName().toString());
    }

    /** Row "List name  [x] Télécharger  [x] Noter  [x] Commenter", everything checked by default. */
    private static HBox rightsRow(String list_name) {
        Label name = new Label(list_name);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox row = new HBox(12, name, spacer);
        for (String right : List.of("Télécharger", "Noter", "Commenter")) {
            CheckBox box = new CheckBox(right);
            box.setSelected(true);
            row.getChildren().add(box);
        }
        row.getStyleClass().add("list-row");
        return row;
    }
}
