package comm_integration;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import client.communication.CommClientController;
import common.dataclasses.Index;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoFormat;
import common.dataclasses.VideoFull;
import common.dataclasses.VideoLight;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.data.InterfaceCommClientCallsDataClient;
import common.interfaces.data.InterfaceCommServerCallsDataServer;
import server.communication.CommServerController;

/**
 * Integration tests of the video download between two clients
 */
class ClientDownloadVideoTest {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int TIMEOUT_MS = 5000;
    private static final String DOWNLOADED_VIDEO_PATH = "downloads/anna-video.mp4";

    private CommServerController server;
    private CommClientController anna_client;
    private CommClientController bob_client;

    private InterfaceCommClientCallsDataClient anna_data_mock;
    private InterfaceCommClientCallsDataClient bob_data_mock;
    private InterfaceCommunicationCallsIHMShare bob_ihm_share_mock;

    private UserLight anna_light;
    private UserLight bob_light;
    private VideoLight anna_video;

    /**
     * Starts the server, connects Anna and Bob to it and waits until both have received the index
     */

    @BeforeEach
    void setUp() throws IOException {
        int port = findFreePort();

        UserFull anna_full = new UserFull("Anna", "pwd", "Anna", null, null);
        UserFull bob_full = new UserFull("Bob", "pwd", "Bob", null, null);
        bob_full.setUploadedVideos(new HashMap<>()); // the map is null by default
        anna_light = anna_full.getSharedInfo();
        bob_light = bob_full.getSharedInfo();
        anna_video = new VideoLight(anna_light, "Anna - video", "Anna", Set.of("test"));

        // building the index (users + videos)
        InterfaceCommServerCallsDataServer data_server_mock = mock(InterfaceCommServerCallsDataServer.class);
        List<UserLight> registered_users = new ArrayList<>();
        List<VideoLight> registered_videos = new ArrayList<>();
        when(data_server_mock.registerUserAndGetIndexStructure(any(), any())).thenAnswer(invocation -> {
            UserLight user = invocation.getArgument(0);
            Set<VideoLight> videos = invocation.getArgument(1);
            registered_users.add(user);
            registered_videos.addAll(videos);
            return new Index(new ArrayList<>(registered_users), new ArrayList<>(registered_videos));
        });

        anna_data_mock = mock(InterfaceCommClientCallsDataClient.class);
        when(anna_data_mock.getMyProfile()).thenReturn(anna_full);
        when(anna_data_mock.askRequesterId()).thenReturn(anna_light.getUuid());

        // when the video is saved it is added to Bob's videos with its local path
        bob_data_mock = mock(InterfaceCommClientCallsDataClient.class);
        when(bob_data_mock.getMyProfile()).thenReturn(bob_full);
        when(bob_data_mock.askRequesterId()).thenReturn(bob_light.getUuid());
        when(bob_data_mock.saveVideo(any())).thenAnswer(invocation -> {
            VideoLight saved_video = new VideoLight(bob_light, anna_video.getTitle(), anna_video.getCreator(), anna_video.getTags());
            bob_full.addUploadedVideo(saved_video.getVideoId(),
                    new VideoFull(saved_video, DOWNLOADED_VIDEO_PATH, VideoFormat.mp4, new HashMap<>(), new HashMap<>()));
            return saved_video;
        });
        bob_ihm_share_mock = mock(InterfaceCommunicationCallsIHMShare.class);

        server = new CommServerController();
        server.setInterfaceCommServerCallsDataServer(data_server_mock);
        server.start(port);

        anna_client = new CommClientController();
        anna_client.setInterfaceCommIHMShare(mock(InterfaceCommunicationCallsIHMShare.class));
        anna_client.setInterfaceCommDataClient(anna_data_mock);

        bob_client = new CommClientController();
        bob_client.setInterfaceCommIHMShare(bob_ihm_share_mock);
        bob_client.setInterfaceCommDataClient(bob_data_mock);

        anna_client.getDataCallsComm().sendProfileInfo(SERVER_IP, port, List.of(anna_video), "Anna");
        verify(anna_data_mock, timeout(TIMEOUT_MS)).saveFullIndex(any(), any());
        bob_client.getDataCallsComm().sendProfileInfo(SERVER_IP, port, new ArrayList<>(), "Bob");
        verify(bob_data_mock, timeout(TIMEOUT_MS)).saveFullIndex(any(), any());
    }

    /**
     * Disconnects the clients and stops the server
     */
    @AfterEach
    void tearDown() {
        if (anna_client != null && anna_client.serverConnection != null) {
            anna_client.serverConnection.disconnect();
        }
        if (bob_client != null && bob_client.serverConnection != null) {
            bob_client.serverConnection.disconnect();
        }
        if (server != null) {
            server.stop();
        }
    }

    /**
     * Bob downloads Anna's video and Bob's IHM Share receives the path of the saved file
     */
    @Test
    @DisplayName("Bob downloads Anna's video and IHM Share receives the path of the file")
    void bobDownloadsAnnaVideo() {
        byte[] video_bytes = {1, 2, 3, 4};
        when(anna_data_mock.askVideo(anna_video.getVideoId(), bob_light.getUuid())).thenReturn(video_bytes);

        bob_client.getIhmShareCallsComm().askVideo(anna_video.getVideoId(), anna_light.getUuid());

        // Anna's client asks Data for the video after Bob asked for it
        verify(anna_data_mock, timeout(TIMEOUT_MS)).askVideo(anna_video.getVideoId(), bob_light.getUuid());

        // Bob's client receives the same bytes as Anna's client
        ArgumentCaptor<byte[]> bytes_captor = ArgumentCaptor.forClass(byte[].class);
        verify(bob_data_mock, timeout(TIMEOUT_MS)).saveVideo(bytes_captor.capture());
        assertArrayEquals(video_bytes, bytes_captor.getValue());

        // Bob's IHM Share uses the video path
        verify(bob_ihm_share_mock, timeout(TIMEOUT_MS)).downloadDone(DOWNLOADED_VIDEO_PATH);
    }

    /**
     * If Anna's can't find the video, nothing is sent back: Bob saves nothing.
     */
    @Test
    @DisplayName("If the owner cannot give the video, Bob saves nothing")
    void ownerCannotGiveTheVideo() {
        when(anna_data_mock.askVideo(any(), any())).thenThrow(new RuntimeException("Video not found"));

        bob_client.getIhmShareCallsComm().askVideo(anna_video.getVideoId(), anna_light.getUuid());

        verify(anna_data_mock, timeout(TIMEOUT_MS)).askVideo(eq(anna_video.getVideoId()), eq(bob_light.getUuid()));
        verify(bob_data_mock, after(1000).never()).saveVideo(any());
        verify(bob_ihm_share_mock, never()).downloadDone(any());
    }

    /**
     * Finds a free port for the server.
     *
     * @return a free port
     * @throws IOException if no port can be opened
     */
    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}