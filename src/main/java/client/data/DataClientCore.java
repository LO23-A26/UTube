package client.data;

import java.util.Set;
import java.util.List;
import java.util.UUID;

import common.dataclasses.ContactList;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoFormat;
import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.dataclasses.Index;
import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import common.interfaces.comm.InterfaceDataCallsComm;



import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import common.interfaces.comm.InterfaceDataCallsComm;

import java.util.Set;

/**
 * @author Lucas Dompnier, Jean Van Gysel
 * @version 2.0
 */
public class DataClientCore {

    private UserFull user;
    private Index index_local;
    private ProfileSaver profileSaver;
    private ContactList contact_lists;
    private final IhmMainCallsDataClientImpl impl_interface_for_ICM;
    private final CommClientCallsDataClientImpl impl_interface_for_comm;
    private InterfaceDataCallsIHMMain interface_from_ICM;
    private InterfaceDataCallsComm interface_from_Comm;
    private IhmShareCallsDataClientImpl impl_interface_for_ICS;

    public DataClientCore() {
        this.index_local = new Index();

        this.profileSaver = new ProfileSaver();

        this.impl_interface_for_ICS = new IhmShareCallsDataClientImpl(this);
        this.impl_interface_for_ICM = new IhmMainCallsDataClientImpl(this);
        this.impl_interface_for_comm = new CommClientCallsDataClientImpl(this);
    }


    void removeRemoteUserFromLocalIndex(UUID user_id){
        if (user_id != null){
            UserLight user_to_remove = null;
            for (UserLight u : index_local.getUserList()) {
                if (user_id.equals(u.getUuid())) {
                    user_to_remove = u;
                    index_local.getUserList().remove(u);
                    break;
                }
            }
            index_local.getVideoList().removeIf(v -> v.getOwner() != null && user_id.equals(v.getOwner().getUuid()));
            if (user_to_remove != null){interface_from_ICM.refreshUserList(user_to_remove);} // ATTENTION ATTENTION voir avec IhmMain pour que signature soit Userlight et pas UserFull

        }
    }

    void saveNewUserLocalIndex(UserLight new_user, List<VideoLight> new_video){
        if (new_user != null){
            index_local.addUser(new_user);
            if (new_video != null){
                index_local.getVideoList().addAll(new_video);
            }
            getInterfaceFromICM().notifyNewUser(new_user,new_video);
        }
    }

    void saveFullLocalIndex(List<UserLight> user_list, List<VideoLight> video_list) {
        index_local.getVideoList().clear();
        index_local.getUserList().clear();
        if (user_list != null) index_local.getUserList().addAll(user_list);
        if (video_list != null) index_local.getVideoList().addAll(video_list);
        if (interface_from_ICM == null) return;
        getInterfaceFromICM().refreshFullDisplay(user_list, video_list);
    }


    //###############################################################


    /**
     * get a video by id
     *
     * @param video_id to retrieve
     * @return the videofull
     */
    public VideoFull getVideoById(UUID video_id){
        return this.user.getUploadedVideos().getOrDefault(video_id,null);
    }
    public IhmMainCallsDataClientImpl getImplInterfaceForICM() {return impl_interface_for_ICM;}

    public CommClientCallsDataClientImpl getImplInterfaceForComm(){return impl_interface_for_comm;}

    public InterfaceDataCallsIHMMain getInterfaceFromICM() {return interface_from_ICM;}

    public InterfaceDataCallsComm getInterfaceFromComm() {return interface_from_Comm;}

    public IhmShareCallsDataClientImpl getImplInterfaceForICS() {return impl_interface_for_ICS;}

    public CommClientCallsDataClientImpl getImplInterfaceForCC() {return impl_interface_for_comm;}

    public void setImplInterfaceForICS(IhmShareCallsDataClientImpl impl_interface_for_ICS) {
        this.impl_interface_for_ICS = impl_interface_for_ICS;
    }

    public void setInterfaceFromICM(InterfaceDataCallsIHMMain interface_from_ICM) {
        this.interface_from_ICM = interface_from_ICM;
    }

    public void setInterfaceFromComm(InterfaceDataCallsComm interface_from_Comm) {
        this.interface_from_Comm = interface_from_Comm;
    }

    public UserFull getUser() {
        return user;
    }

    public void setUser(UserFull user) {
        this.user = user;
    }

    public Index getIndexLocal(){ return index_local;
    }

    public List<VideoLight> getListVideo() {
        return index_local.getVideoList();
    }

    public void setListVideo(List<VideoLight> list_video) {
        this.index_local.setVideoList( list_video);
    }

    public List<UserLight> getConnectedUser() {
        return index_local.getUserList();
    }

    public void setConnectedUser(List<UserLight> connected_user) {
        this.index_local.setUserList(connected_user);
    }

    public ProfileSaver getProfileSaver() {
        return profileSaver;
    }

    public void setProfileSaver(ProfileSaver profileSaver) {
        this.profileSaver = profileSaver;
    }

    public ContactList getContactLists() {
        return contact_lists;
    }

    public void setContactLists(ContactList contact_lists) {
        this.contact_lists = contact_lists;
    }

    UUID getCurrentUserId() {
        if (user == null) {
            System.err.println("getCurrentUserId : aucun utilisateur connecté");
            return null;
        }
        return user.getSharedInfo().getUuid();
    }

    void addingNewVideo(Path file, String title, String creator, Set<String> tags) {

        // 1. Create a VideoFull object for the video file
        VideoFull video = createVideo(file, title, creator, tags);
        // 2. Add it to the list + save User's Profile
        saveLocally(video);
        // 3. Send it to Comm for sharing purposes. If this fail, you'll know
        boolean announced = announceVideo(video.getSharedInfo());
        if (!announced) {
            // Si problème dans l'annonce, la vidéo est conservée, pas de retour en arrière
            System.err.println("addingNewVideo : « " + video.getSharedInfo().getTitle()
                    + " » enregistrée en local mais pas annoncée aux autres utilisateurs. "
                    + "Elle sera envoyée à la prochaine connexion.");
        }

    }

    VideoFull createVideo(Path file, String title, String creator, Set<String> tags) {
        if (user == null) {
            throw new IllegalStateException("createVideo : aucun utilisateur connecté");
        }
        if (file == null || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("createVideo : fichier introuvable : " + file);
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("createVideo : titre obligatoire");
        }

        VideoFormat format = VideoFormat.checkFormatFromPath(file);

        Set<String> video_tags = new HashSet<>();
        if (tags != null) {
            video_tags.addAll(tags);
        }

        // Titre sans espaces au début et à la fin
        String clean_title = title.strip();

        VideoLight info = new VideoLight(user.getSharedInfo(), clean_title, creator, video_tags);

        // Chemin complet du fichier
        String file_path = file.toAbsolutePath().toString();

        // Commentaire et Notes vides au départ
        Map<UUID, Rating> empty_ratings = new HashMap<>();
        Map<UUID, Comment> empty_comments = new HashMap<>();

        return new VideoFull(info, file_path, format, empty_ratings, empty_comments);
	}

    boolean announceVideo(VideoLight video_info) {
        if (interface_from_Comm == null) {
            return false;
        }
        try {
            interface_from_Comm.sendNewVideo(video_info);
            return true;
        } catch (RuntimeException e) {
            System.err.println("announceVideo :" + e.getMessage());
            return false;
        }
    }

    void saveLocally(VideoFull video) {
        if (user != null) {

            UUID video_id = video.getSharedInfo().getVideoId();
            user.addUploadedVideo(video_id, video);
            user.addSharedVideo(video.getSharedInfo());
            try {
                profileSaver.saveProfile(user);
            } catch (RuntimeException e) {
                user.removeUploadedVideo(video_id);
                throw e;
            }
        }
    }

    // Rename of removeSharedVideo(video_id : UUID)
    void removeRemoteVideoFromLocalIndex(UUID video_id) {
        if (video_id != null && getListVideo() != null) {
            boolean removed = false;
            for (VideoLight v : getListVideo()) {
                if (video_id.equals(v.getVideoId())) {
                    getListVideo().remove(v);
                    removed = true;
                    break;
                }
            }

            if (removed && interface_from_ICM != null) {
                interface_from_ICM.refreshSharedVideos(getListVideo());
            }
        }
    }

    // Rename of addSharedVideo(video : VideoLight)
    void addRemoteVideoToLocalIndex(VideoLight video_info) {
        if (video_info != null && video_info.getVideoId() != null) {
            removeRemoteVideoFromLocalIndex(video_info.getVideoId());

            getListVideo().add(video_info);

            if (interface_from_ICM != null) {
                interface_from_ICM.displayNewVideo(video_info);
            }
        }

    }

    private String normalizeText(String input) {
        if (input == null) {
            return null;
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase().trim();
    }

    public List<VideoLight> searchSharedVideos(String title, String creator, Set<String> tags) {
        List<VideoLight> matching_videos = new ArrayList<>();

        if (getListVideo() == null || getListVideo().isEmpty()) {
            return matching_videos;
        }

        // Clean and normalize title filter
        String search_title = null;
        if (title != null && !title.isBlank()) {
            search_title = normalizeText(title);
        }

        // Clean and normalize creator filter
        String search_creator = null;
        if (creator != null && !creator.isBlank()) {
            search_creator = normalizeText(creator);
        }

        // Clean and normalize tags filter
        Set<String> search_tags = new HashSet<>();
        if (tags != null) {
            for (String tag : tags) {
                if (tag != null && !tag.isBlank()) {
                    search_tags.add(normalizeText(tag));
                }
            }
        }

        // If no filter is provided, return all shared videos
        if (search_title == null && search_creator == null && search_tags.isEmpty()) {
            matching_videos.addAll(getListVideo());
            return matching_videos;
        }

        // Filter videos against search criteria
        for (VideoLight video : getListVideo()) {
            if (video == null) {
                continue;
            }

            // Check title filter
            if (search_title != null) {
                if (video.getTitle() == null) {
                    continue;
                }
                String normalized_title = normalizeText(video.getTitle());
                if (!normalized_title.contains(search_title)) {
                    continue;
                }
            }

            // Check creator filter
            if (search_creator != null) {
                if (video.getCreator() == null) {
                    continue;
                }
                String normalized_creator = normalizeText(video.getCreator());
                if (!normalized_creator.contains(search_creator)) {
                    continue;
                }
            }

            // Check tags filter: matches if video contains at least one searched tag
            if (!search_tags.isEmpty()) {
                if (video.getTags() == null || video.getTags().isEmpty()) {
                    continue;
                }

                boolean has_matching_tag = false;
                for (String video_tag : video.getTags()) {
                    if (video_tag != null) {
                        String normalized_video_tag = normalizeText(video_tag);
                        if (search_tags.contains(normalized_video_tag)) {
                            has_matching_tag = true;
                            break;
                        }
                    }
                }

                if (!has_matching_tag) {
                    continue;
                }
            }

            matching_videos.add(video);
        }

        return matching_videos;
    }
}
