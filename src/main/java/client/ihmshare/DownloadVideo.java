package client.ihmshare;

import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.comm.InterfaceIHMShare_calls_Comm;
import javafx.scene.control.Alert;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manager of the downloads of IHM-Share (sequence "Télécharger vidéo").
 * <p>
 * Keeps the downloads asked to Communication and not answered yet, and the videos
 * already downloaded during the session (displayed in "Mes téléchargements", screen 07).
 * Must only be used from the JavaFX thread.
 * <p>
 * {@code InterfaceCommunicationCallsIHMShare.downloadDone} only gives the local path of the
 * file, and {@code errorAccess} only the error, not the video: the answers are matched with
 * the requests in the order of the requests.
 *
 * @author IHM-Share
 * @version 1.0
 */
public class DownloadVideo {

    /** Result of a download request. */
    public enum Request {
        /** Request sent to Communication, the answer will arrive in {@link #downloadDone}. */
        SENT,
        /** A download of this video is already in progress. */
        ALREADY_PENDING,
        /** Communication is not plugged or the owner of the video is unknown. */
        UNAVAILABLE
    }

    private final IHMShareCore core;
    /** Videos asked to Communication, in the order of the requests. */
    private final Deque<VideoLight> pending = new ArrayDeque<>();
    /** Videos downloaded during the session, by video id, in the order of the downloads. */
    private final Map<UUID, VideoFull> downloaded = new LinkedHashMap<>();

    /**
     * Class constructor. Keep a pointer to IHMShareCore object
     *
     * @param core (IHMShareCore) pointer to the parent object
     */
    public DownloadVideo(IHMShareCore core) {
        this.core = core;
    }

    /**
     * Ask Communication to download a video from its owner
     *
     * @param video (VideoLight) video to download
     * @return (Request) result of the request
     */
    public Request download(VideoLight video) {
        InterfaceIHMShare_calls_Comm comm = core.getInterfaceFromCommunication();
        if (video == null || comm == null || video.getOwner() == null) {
            return Request.UNAVAILABLE;
        }
        if (isPending(video.getVideoId())) {
            return Request.ALREADY_PENDING;
        }
        pending.addLast(video);
        try {
            comm.askVideo(video.getVideoId(), video.getOwner().getUuid());
        } catch (RuntimeException e) {
            pending.removeLastOccurrence(video);
            return Request.UNAVAILABLE;
        }
        return Request.SENT;
    }

    /**
     * Answer of Communication: the oldest pending download is done. The video is added to
     * "Mes téléchargements" and the opened screens are refreshed
     *
     * @param video_path (String) local path of the downloaded video
     */
    public void downloadDone(String video_path) {
        VideoLight video = pending.pollFirst();
        if (video == null) {
            return; // no request in progress: unexpected answer
        }
        VideoFull video_full = downloaded.get(video.getVideoId());
        if (video_full == null) {
            video_full = new VideoFull(video, video_path, null, new HashMap<>(), new HashMap<>());
            downloaded.put(video.getVideoId(), video_full);
        } else {
            video_full.setFilePath(video_path);
        }

        DisplayVideoController display = core.getControllerDisplayVideo();
        if (display != null) {
            display.downloadDone(video.getVideoId(), video_path);
        }
        DownloadsController downloads = core.getControllerDownloads();
        if (downloads != null) {
            downloads.refreshView();
        }
    }

    /**
     * Answer of Communication: the owner refused the oldest pending download (error 403).
     * The request is dropped and the user is informed
     *
     * @param error (Exception) error sent back by the owner
     */
    public void accessDenied(Exception error) {
        VideoLight video = pending.pollFirst();
        if (video == null) {
            return; // no request in progress: unexpected answer
        }
        DisplayVideoController display = core.getControllerDisplayVideo();
        if (display != null) {
            display.refreshView(); // the "Télécharger" button is enabled again
        }
        Alert alert = new Alert(Alert.AlertType.ERROR,
                "Le ou la propriétaire de « " + video.getTitle() + " » vous a refusé l'accès à cette vidéo.");
        alert.setHeaderText("Téléchargement refusé");
        alert.show();
    }

    /**
     * @param video_id (UUID) id of the video
     * @return (boolean) true if a download of this video is in progress
     */
    public boolean isPending(UUID video_id) {
        return pending.stream().anyMatch(video -> video.getVideoId().equals(video_id));
    }

    /**
     * @param video_id (UUID) id of the video
     * @return (VideoFull) the downloaded video, null if it was not downloaded during the session
     */
    public VideoFull getDownloaded(UUID video_id) {
        return downloaded.get(video_id);
    }

    /**
     * @return (List of VideoLight) videos downloaded during the session, in the order of the downloads
     */
    public List<VideoLight> getDownloadedVideos() {
        List<VideoLight> videos = new ArrayList<>();
        for (VideoFull video : downloaded.values()) {
            videos.add(video.getSharedInfo());
        }
        return videos;
    }

    /**
     * Remove a video from "Mes téléchargements"
     *
     * @param video_id (UUID) id of the video
     * @return (VideoFull) the removed video, null if it was not downloaded
     */
    public VideoFull removeDownloaded(UUID video_id) {
        return downloaded.remove(video_id);
    }
}
