package common.dataclasses.communication.messages;

import static java.lang.System.Logger.Level.ERROR;
import static java.lang.System.Logger.Level.INFO;

import client.communication.CommClientController;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.ClientMessage;
import common.dataclasses.VideoFull;
import common.dataclasses.UserFull;


public class DownloadVideoResponseMessage implements ClientMessage {
    private static final System.Logger logger = System.getLogger(DownloadVideoResponseMessage.class.getName());

    private final byte[] video_bytes;

    /**
     * @param video_bytes the video file and its data, as returned by Data in askVideo()
     */
    public DownloadVideoResponseMessage(byte[] video_bytes) {
        this.video_bytes = video_bytes;
    }

    /**
     * Handles the answer on the requester's side.
     * @param controller the client controller
     */
    @Override
    public void handle(CommClientController controller) {
        if (video_bytes == null || video_bytes.length == 0) {
            logger.log(ERROR, "Received an empty video");
            return;
        }

        VideoLight saved_video;
        try {
            saved_video = controller.interfaceCommDataClient.saveVideo(video_bytes);
        } catch (RuntimeException ex) {
            logger.log(ERROR, "Could not save the downloaded video", ex);
            return;
        }
        logger.log(INFO, "Video downloaded and saved: {0}", saved_video);

        // data does not return the path of the saved file (it returns a VideoLight) : have to find it in the videos map of the local user
        UserFull my_profile = controller.interfaceCommDataClient.getMyProfile();
        VideoFull saved_video_full = null;
        if (saved_video != null && my_profile != null && my_profile.getUploadedVideos() != null) {
            saved_video_full = my_profile.getUploadedVideos().get(saved_video.getVideoId());
        }
        if (saved_video_full == null) {
            logger.log(ERROR, "Could not find the downloaded video in the local profile");
            return;
        }


        controller.interfaceCommIHMShare.downloadDone(saved_video_full.getFilePath());
    }
}






