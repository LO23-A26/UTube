package common.dataclasses;

import java.util.UUID;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.logging.Logger;

/**
 * @author Lucas Dompnier
 * @version 1.0
 */
public class UserLight {

    private UUID uuid;
    private String pseudo;
    private BufferedImage avatar;

    Logger logger = Logger.getLogger(getClass().getName());

    UserLight(String pseudo, String avatar_path) {
        this.pseudo = pseudo;
        this.uuid = this.generateUuid();
        setAvatar(avatar_path);
    }

    UserLight(String pseudo) {
        this.pseudo = pseudo;
        this.uuid = this.generateUuid();
    }

    public BufferedImage getAvatar() {
        return avatar;
    }

    /**
     * Find the file with the path and put it in the avatar field
     *
     * @param avatar_path: path to the avatar image on the user's computer
     */
    public void setAvatar(String avatar_path) {
        try {
            // Load the image from the specified file path
            this.avatar = ImageIO.read(new File(avatar_path));
        } catch (Exception e) {
            logger.warning("Error loading image: " + e.getMessage());
        }
    }

    /**
     * Generate a UUID with function from java.util.UUID
     * @return the UUID generated
     */
    public UUID generateUuid() {
        return UUID.randomUUID();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
