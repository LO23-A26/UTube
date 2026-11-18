package client.data;

import common.dataclasses.UserLight;
import common.dataclasses.UserFull;
import common.dataclasses.VideoLight;
import common.dataclasses.VideoFull;
import common.dataclasses.Comment;
import common.dataclasses.Rating;
import common.exceptions.AccessDeniedException;
import common.interfaces.data.InterfaceCommClientCallsDataClient;
import common.dataclasses.VideoFormat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of the Comm Client calls Dataclient interface
 *
 * @author Eloïse Bonnet
 * @version 1.0
 */
public class CommClientCallsDataClientImpl implements InterfaceCommClientCallsDataClient {

    private DataClientCore data_client_core;
    

    /**
     * Gives details access for a specific video to a requester.
     *
     * @param requester_profile The profile of the user requesting access.
     * @param video_id The UUID of the video.
     * @return The full video with its metadata, ratings and comments.
     * @throws AccessDeniedException If the requester is not allowed or the video is unknown.
     */
    public VideoFull giveDetailsAccess(UserLight requester_profile, UUID video_id) throws AccessDeniedException {
        if (!checkAccess(requester_profile)) {
            throw new AccessDeniedException("Access denied to video " + video_id);
        }
        Map<UUID, VideoFull> uploaded_videos = data_client_core.getUser().getUploadedVideos();
        if (uploaded_videos == null || !uploaded_videos.containsKey(video_id)) {
            throw new AccessDeniedException("Unknown video " + video_id);
        }
        return uploaded_videos.get(video_id);
    }

    /**
     * Checks whether a user may access a video. Everybody is allowed in V2.
     *
     * @param requester_profile The profile of the user requesting access.
     * @return true if the access is authorized.
     */
    private boolean checkAccess(UserLight requester_profile) {
        return true;
    }

    /**
     * Adds a comment to a video.
     *
     * @param comment The comment object to be added.
     */
    public void addComment(Comment comment){}

    /**
     * Edits an existing comment.
     *
     * @param comment The comment object containing updated data.
     */
    public void editComment(Comment comment){}

    /**
     * Adds a rating to a video.
     *
     * @param rating The rating object to be added.
     */
    public void addRating(Rating rating){}

    /**
     * Edits an existing rating.
     *
     * @param rating The rating object containing updated data.
     */
    public void editRating(Rating rating){}

    /**
     * Deletes a comment from a video based on video ID and author ID.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the comment's author.
     */
    public void deleteComment(UUID video_id, UUID author_uid){}

    /**
     * Deletes a rating from a video.
     *
     * @param video_id The UUID of the video.
     * @param author_uid The UUID of the rating's author.
     */
    public void deleteRating(UUID video_id, UUID author_uid){}

    /**
     * Marks a video as shared.
     *
     * @param video_info The lightweight video information object.
     */
    public void markAsShared(VideoLight video_info){}

    /**
     * Adds a remote video to the system.
     *
     * @param video_info The lightweight video information object.
     */
    public void addRemoteVideo(VideoLight video_info){
        if (data_client_core != null) {
            data_client_core.addRemoteVideoToLocalIndex(video_info);
        }
    }

    /**
     * Removes a video from a user's collection.
     *
     * @param video_id The UUID of the video to remove.
     */
    public void removeVideoFromUser(UUID video_id){
        if (video_id != null && this.data_client_core != null){
            this.data_client_core.removeRemoteVideoFromLocalIndex(video_id);
        }
    }

    /**
     * Asks for the requester's ID.
     *
     * @return UUID representing the requester.
     */
    public UUID askRequesterId(){
        if (data_client_core == null) {
            System.err.println("askRequesterId : DataClientCore non branché");
            return null;
        }
        return data_client_core.getCurrentUserId();
    }

    /**
     * Requests a video's data.
     *
     * @param video_id The UUID of the video.
     * @param requester_id The UUID of the user requesting the video.
     * @return Byte array containing video data.
     */
    public byte[] askVideo(UUID video_id, UUID requester_id){

        // Recover the video thanks to video_id
        VideoFull v;
        try {
            v = data_client_core.getUser().getUploadedVideos().get(video_id);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }

        // Check is path format is supported
        if (!VideoFormat.isSupported(Path.of(v.getFilePath()))){
            throw new RuntimeException("Error : the path of the video is not supported.");
        }

        // Serialisation of mp4 and v
        File file = new File(v.getFilePath());
        byte[] mp4_bytes;
        try {
            mp4_bytes = Files.readAllBytes(file.toPath());
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

        String video_title = v.getSharedInfo().getTitle();

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(video_title + ".txt");
        }
        catch (FileNotFoundException e){
            throw new RuntimeException(e);
        }

        ObjectOutputStream oos;
        try{
            oos = new ObjectOutputStream(fos);
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

        try {
            oos.writeObject(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] video_object_bytes;
        ByteArrayOutputStream video_object_byte_array_output_stream;
        try {
            video_object_byte_array_output_stream = new ByteArrayOutputStream();
            video_object_byte_array_output_stream.writeTo(oos);
            video_object_bytes = video_object_byte_array_output_stream.toByteArray();
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

        int length_mp4_bytes = mp4_bytes.length;
        int length_object_bytes = video_object_bytes.length;


        byte[] video_object_and_mp4_bytes = new byte[length_mp4_bytes + length_object_bytes + 100];
        for (int i = 0 ; i < length_mp4_bytes ; i++){
            video_object_and_mp4_bytes[i] = mp4_bytes[i];
        }

        // Separateur arbitraire : 100 * "-128"
        for (int i = length_mp4_bytes ; i < length_mp4_bytes + 100 ; i++){
            video_object_and_mp4_bytes[i] = -128;
        }
        for (int i = length_mp4_bytes + 100 ; i < length_object_bytes ; i++){
            video_object_and_mp4_bytes[i] = video_object_bytes[i];
        }

        return video_object_and_mp4_bytes;
    }

    /**
     * Saves a video file to the system.
     *
     * @param video_file The byte array of the video file data.
     * @return VideoLight object representing the saved video.
     */
    public VideoLight saveVideo(byte[] video_file){
        // Il faut changer l'id du VideoFull (generation aleatoire)
        // Download video: save la video dans le dossier local de l'user (voir ProfileSaver)
        // Deserialiser et telecharger le path
        // Deserialiser l'id
        // SaveLocally(VideoFull video) ?
        return null;
    }

    /**
     * Saves a new user and their associated new video.
     *
     * @param new_user The lightweight user profile.
     * @param new_video The lightweight video information.
     */
    public void saveNewUser(UserLight new_user, List<VideoLight> new_video){
        data_client_core.getInterfaceFromICM().notifyNewUser(new_user,new_video);
    }

    /**
     * Saves a full index of users and videos.
     *
     * @param user_list The list of user profiles.
     * @param video_list The list of videos.
     */
    public void saveFullIndex(List<UserLight> user_list, List<VideoLight> video_list){
        data_client_core.getInterfaceFromICM().refreshFullDisplay(user_list,video_list);
    }

    /**
     * Retrieves the current user's full profile.
     *
     * @return UserFull object representing the current user.
     */
    public UserFull getMyProfile(){
        return null;
    }

    /**
     * Saves an updated user profile.
     *
     * @param user_profile The updated full user profile.
     */
    public void saveProfileUpdated(UserFull user_profile){}

    /**
     * Removes a user from the system.
     *
     * @param user_profile The full user profile to remove.
     */
    public void removeUser(UserFull user_profile){
        data_client_core.getInterfaceFromICM().refreshUserList(user_profile);
    }

    /**
     * Removes a shared video.
     *
     * @param video_id The UUID of the video to remove.
     */

    public void removeSharedVideo(UUID video_id){}

    /**
     * Gets the Data client core used by this implementation.
     *
     * @return The Data client core.
     */
    public DataClientCore getDataClientCore() {
        return data_client_core;
    }

    /**
     * Sets the Data client core used by this implementation.
     *
     * @param data_client_core The Data client core to use.
     */
    public void setDataClientCore(DataClientCore data_client_core) {
        this.data_client_core = data_client_core;
    }
}
