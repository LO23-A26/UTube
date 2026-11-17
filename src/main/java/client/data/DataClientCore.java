package client.data;

import common.dataclasses.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import common.interfaces.IHMMain.InterfaceDataCallsIHMMain;
import common.interfaces.comm.InterfaceDataCallsComm;

import java.util.Set;

/**
 * @author Lucas Dompnier
 * @version 1.0
 */
public class DataClientCore {

    private UserFull user;
    private Set<VideoLight> list_video;
    private Set<UserLight> connected_user;
    private ProfileSaver profileSaver;
    private ContactList contact_lists;
    private IhmMainCallsDataClientImpl impl_interface_for_ICM;
    private InterfaceDataCallsIHMMain interface_from_ICM;
    private InterfaceDataCallsComm interface_from_Comm;
    private CommClientCallsDataClientImpl impl_interface_for_CC;
    private IhmShareCallsDataClientImpl impl_interface_for_ICS;



    public DataClientCore() {
        this.list_video = new HashSet<>();
        this.connected_user = new HashSet<>();
        this.profileSaver = new ProfileSaver();

        this.impl_interface_for_ICM = new IhmMainCallsDataClientImpl();
        this.impl_interface_for_ICM.setDataClientCore(this);
        this.impl_interface_for_CC = new CommClientCallsDataClientImpl();
        this.impl_interface_for_CC.setDataClientCore(this);
        this.impl_interface_for_ICS = new IhmShareCallsDataClientImpl();
        this.impl_interface_for_ICS.setDataClientCore(this);
    }

    public IhmMainCallsDataClientImpl getImplInterfaceForICM() {
        return impl_interface_for_ICM;
    }
    public InterfaceDataCallsIHMMain getInterfaceFromICM() {
        return interface_from_ICM;
    }
    public InterfaceDataCallsComm getInterfaceFromComm() {
        return interface_from_Comm;
    }
    
    public IhmShareCallsDataClientImpl getImplInterfaceForICS() {
    return impl_interface_for_ICS;
}
    public CommClientCallsDataClientImpl getImplInterfaceForCC() { return impl_interface_for_CC; }



	public void setImplInterfaceForICS(IhmShareCallsDataClientImpl impl_interface_for_ICS) {
		this.impl_interface_for_ICS = impl_interface_for_ICS;
	}

    public void setImplInterfaceForICM(IhmMainCallsDataClientImpl impl_interface_for_ICM) {
        this.impl_interface_for_ICM = impl_interface_for_ICM;
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

    public Set<VideoLight> getListVideo() {
        return list_video;
    }

    public void setListVideo(Set<VideoLight> list_video) {
        this.list_video = list_video;
    }

    public Set<UserLight> getConnectedUser() {
        return connected_user;
    }

    public void setConnectedUser(Set<UserLight> connected_user) {
        this.connected_user = connected_user;
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

    public void setContactLists(ContactList contact_lists) {this.contact_lists = contact_lists;}

    UUID getCurrentUserId() {
        if (user == null) {
            System.err.println("getCurrentUserId : aucun utilisateur connecté");
            return null;
        }
        return user.getSharedInfo().getUuid();
    }

    void addingNewVideo(Path file, String title, String creator, Set<String> tags) {

        //1. Create a VideoFull object for the video file
		VideoFull video = createVideo(file, title, creator, tags);
        //2. Add it to the list + save User's Profile
		saveLocally(video);
        //3. Send it to Comm for sharing purposes. If this fail, you'll know
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

        VideoFull video = new VideoFull(info, file_path, format, empty_ratings, empty_comments);
        return video;
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
    void removeRemoteVideoFromLocalIndex(UUID video_id){
        if (video_id != null){
            for (VideoLight v : list_video) {
                if (video_id.equals(v.getVideoId())) {
                    list_video.remove(v);
                    break;
                }
            }
        }
    }


    void addRemoteVideoToLocalIndex(VideoLight video_info) {
        if (video_info != null && video_info.getVideoId() != null) {
            removeRemoteVideoFromLocalIndex(video_info.getVideoId());

            list_video.add(video_info);

            if (interface_from_ICM != null) {
                interface_from_ICM.displayNewVideo(video_info);
            }
        }

    }



}
