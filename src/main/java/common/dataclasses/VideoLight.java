package common.dataclasses;

import java.util.Set;
import java.util.UUID;

/**
 * Lightweight representation of a video containing public metadata.
 *
 * @author Jean Van Gysel, Kilian Lagrevol
 * @version 2.0
 */
public class VideoLight {
    private UUID video_id;
    private UserLight owner;
    private String title;
    private String creator;
    private Set<String> tags;

    public VideoLight() {
    }

    public VideoLight(UUID video_id, UserLight owner, String title, String creator, Set<String> tags) {
        this.video_id = video_id;
        this.owner = owner;
        this.title = title;
        this.creator = creator;
        this.tags = tags;
    }

    public VideoLight(UserLight owner, String title, String creator, Set<String> tags) {
        this.video_id = UUID.randomUUID();
        this.owner = owner;
        this.title = title;
        this.creator = creator;
        this.tags = tags;
    }

    public VideoLight(UserLight owner, String title, String creator) {
        this.video_id = UUID.randomUUID();
        this.owner = owner;
        this.title = title;
        this.creator = creator;
    }

    public UUID getVideoId() {
        return video_id;
    }

    public void setVideoId(UUID video_id) {
        this.video_id = video_id;
    }

    public UserLight getOwner() {
        return owner;
    }

    public void setOwner(UserLight owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
