package main.common.dataclasses;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author Lucas Dompnier
 * @version 2.0
 */
class UserFull {

    private UserLight shared_info;
    private String password;

    private String first_name;
    private String last_name;
    private LocalDate birth_date;

    private Set<VideoLight> shared_videos;
    private Map<UUID, VideoFull> uploaded_videos;
    private Map<AccessRight, ContactList> contact_lists;

    public UserFull(String pseudo, String avatar_path, String password, String first_name, String last_name, LocalDate birth_date) {
        this.shared_info = new UserLight(pseudo, avatar_path);
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
    }

    public UserFull(String pseudo, String password, String first_name, String last_name, LocalDate birth_date) {
        this.shared_info = new UserLight(pseudo);
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birth_date = birth_date;
    }

    /**
     * Add video to the list of shared ones
     *
     * @param video: the video to add
     */
    public void addSharedVideo(VideoLight video) {
        this.shared_videos.add(video);
    }

    /**
     * Add video to the map of uploaded ones with the video uuid
     *
     * @param video_uuid: the uuid of the video to add
     * @param video: the video to add
     */
    public void addUploadedVideo(UUID video_uuid, VideoFull video) {
        this.uploaded_videos.put(video_uuid, video);
    }

    /**
     * Add a contact list to the map of contact lists with the access right
     *
     * @param access_right: ...
     * @param contact_list: ...
     */
    public void addContactList(AccessRight access_right, ContactList contact_list) {
        this.contact_lists.put(access_right, contact_list);
    }

    /**
     * Get a LocalDate based on year + month + day
     *
     * @param year: the year
     * @param month: the month
     * @param day: the day
     * @return Date based on the year, the month and the day in params
     */
    public LocalDate getLocalDateFromDateDetails(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    public void setContactLists(Map<AccessRight, ContactList> contact_lists) {
        this.contact_lists = contact_lists;
    }

    public Map<AccessRight, ContactList> getContactLists() {
        return contact_lists;
    }

    public void setSharedVideos(Set<VideoLight> shared_videos) {
        this.shared_videos = shared_videos;
    }

    public Set<VideoLight> getSharedVideos() {
        return shared_videos;
    }

    public Map<UUID, VideoFull> getUploadedVideos() {
        return uploaded_videos;
    }

    public void setUploadedVideos(Map<UUID, VideoFull> uploaded_videos) {
        this.uploaded_videos = uploaded_videos;
    }

    public void setBirthDate(LocalDate birth_date) {
        this.birth_date = birth_date;
    }

    public LocalDate getBirthDate() {
        return birth_date;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLastName() {
        return this.last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getFirstName() {
        return this.first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public UserLight getSharedInfo() {
        return shared_info;
    }

    public void setSharedInfo(UserLight shared_info) {
        this.shared_info = shared_info;
    }
}