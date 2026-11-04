package client.ihmshare;

import common.dataclasses.VideoFormat;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Controller of the "Add a share" screen (09).
 * Checks the upload form (video file, title, optional creator and tags)
 * then asks Data to add the video.
 *
 * @author IHM-Share
 * @author Romain Tesseyre
 * @version 2.1
 */
public class AddVideoController {
    private IHMShareCore core;

    /** Video file chosen by the user, null while no file is chosen. */
    private Path selected_file;
    /** Title typed by the user (mandatory). */
    private String title;
    /** Creator typed by the user (optional). */
    private String creator;
    /** Tags typed by the user, separated by commas (optional). */
    private String tags_text;

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the parent object
     */
    public AddVideoController(IHMShareCore core) {
        this.core = core;
    }

    /**
     * Checks the upload form, then sends the video to Data.
     * The video file and the title are mandatory, the creator and the tags are optional.
     * If the file format is not supported or a mandatory field is missing,
     * an error is displayed and nothing is sent.
     */
    public void checkVideoForm() {
        if (selected_file == null) {
            displayError("Choisissez un fichier vidéo.");
            return;
        }
        if (!checkFormat(selected_file)) {
            displayError("Format non supporté : MP4 ou AVI uniquement.");
            return;
        }
        if (title == null || title.isBlank()) {
            displayError("Le titre est obligatoire.");
            return;
        }

        String video_creator = "";
        if (creator != null) {
            video_creator = creator.strip();
        }
        Set<String> tags = parseTags(tags_text);

        core.getInterfaceFromData().addVideo(selected_file, title.strip(), video_creator, tags);
    }

    /**
     * Checks that the file has a supported video format (see common.dataclasses.VideoFormat).
     * The check is case-insensitive: "clip.MP4" is accepted.
     *
     * @param file the video file chosen by the user
     * @return true if the extension is a supported format, false otherwise
     */
    private boolean checkFormat(Path file) {
        Path file_name = file.getFileName();
        if (file_name == null) {
            return false;
        }
        String name = file_name.toString();
        int dot_index = name.lastIndexOf('.');
        if (dot_index == -1) {
            return false;
        }
        String extension = name.substring(dot_index + 1).toLowerCase();
        for (VideoFormat format : VideoFormat.values()) {
            if (format.name().equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Displays an error message on the upload form.
     *
     * @param message the error to show to the user
     */
    private void displayError(String message) {
        // TODO: show the message in the view once it exists
        System.err.println(message);
    }

    /**
     * Turns the text typed in the tags field into the set of tags expected by Data addVideo.
     * Tags are separated by commas, surrounding spaces are removed,
     * empty tags and duplicates are ignored.
     *
     * @param tags_text text typed by the user, e.g. "music, java ,music"
     * @return the set of tags, e.g. {"music", "java"}; an empty set if there is no tag
     */
    private Set<String> parseTags(String tags_text) {
        Set<String> tags = new HashSet<>();
        if (tags_text == null) {
            return tags;
        }
        for (String part : tags_text.split(",")) {
            String tag = part.strip();
            if (!tag.isEmpty()) {
                tags.add(tag);
            }
        }
        return tags;
    }
}
