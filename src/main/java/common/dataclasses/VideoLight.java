package common.dataclasses;

import java.util.Set;
import java.util.UUID;

public class VideoLight {
    private UUID video_id;
    private UserLight owner;
    private String title;
    private String creator;
    private Set<String> tags;

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
