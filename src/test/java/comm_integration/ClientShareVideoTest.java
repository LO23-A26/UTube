package comm_integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import client.communication.CommClientController;
import common.dataclasses.Index;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.dataclasses.communication.messages.ShareVideoMessage;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.data.InterfaceCommClientCallsDataClient;
import common.interfaces.data.InterfaceCommServerCallsDataServer;
import server.communication.CommServerController;

class ClientShareVideoTest {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int TIMEOUT_MS = 5000;

    private int port;
    private CommServerController server;
    private CommClientController annaClient;
    private CommClientController bobClient;

    private InterfaceCommServerCallsDataServer dataServerMock;
    private InterfaceCommClientCallsDataClient annaDataClientMock;
    private InterfaceCommClientCallsDataClient bobDataClientMock;

    private UserLight annaLight;
    private UserLight bobLight;
    private List<VideoLight> annaVideos;
    private List<VideoLight> bobVideos;

    @BeforeEach
    void setUp() throws IOException {
        port = findFreePort();

        UserFull annaFull = new UserFull("Anna", "pwd", "Anna", null, null);
        UserFull bobFull = new UserFull("Bob", "pwd", "Bob", null, null);
        annaLight = annaFull.getSharedInfo();
        bobLight = bobFull.getSharedInfo();

        annaVideos = new ArrayList<>(List.of(
                new VideoLight(annaLight, "Anna - video 1", "Anna", Set.of("test", "comm"))));
        bobVideos = new ArrayList<>(List.of(
                new VideoLight(bobLight, "Bob - video 1", "Bob", Set.of("test", "comm"))));

        dataServerMock = mock(InterfaceCommServerCallsDataServer.class);
        annaDataClientMock = mock(InterfaceCommClientCallsDataClient.class);
        bobDataClientMock = mock(InterfaceCommClientCallsDataClient.class);

        List<UserLight> registeredUsers = new ArrayList<>();
        List<VideoLight> registeredVideos = new ArrayList<>();
        when(dataServerMock.registerUserAndGetIndexStructure(any(), any())).thenAnswer(invocation -> {
            UserLight user = invocation.getArgument(0);
            @SuppressWarnings("unchecked")
            Set<VideoLight> videos = invocation.getArgument(1);

            registeredUsers.removeIf(existing -> existing.getUuid().equals(user.getUuid()));
            registeredUsers.add(user);

            registeredVideos.removeIf(existing -> existing.getOwner() != null
                    && existing.getOwner().getUuid().equals(user.getUuid()));
            registeredVideos.addAll(videos);

            return new Index(new ArrayList<>(registeredUsers), new ArrayList<>(registeredVideos));
        });

        when(annaDataClientMock.getMyProfile()).thenReturn(annaFull);
        when(annaDataClientMock.askRequesterId()).thenReturn(annaLight.getUuid());

        when(bobDataClientMock.getMyProfile()).thenReturn(bobFull);
        when(bobDataClientMock.askRequesterId()).thenReturn(bobLight.getUuid());

        server = new CommServerController();
        server.setInterfaceCommServerCallsDataServer(dataServerMock);
        server.start(port);

        annaClient = new CommClientController();
        annaClient.setInterfaceCommIHMShare(mock(InterfaceCommunicationCallsIHMShare.class));
        annaClient.setInterfaceCommDataClient(annaDataClientMock);

        bobClient = new CommClientController();
        bobClient.setInterfaceCommIHMShare(mock(InterfaceCommunicationCallsIHMShare.class));
        bobClient.setInterfaceCommDataClient(bobDataClientMock);

        annaClient.getDataCallsComm().sendProfileInfo(SERVER_IP, port, annaVideos, "Anna");
        bobClient.getDataCallsComm().sendProfileInfo(SERVER_IP, port, bobVideos, "Bob");

        verify(annaDataClientMock, timeout(TIMEOUT_MS)).saveFullIndex(any(), any());
        verify(bobDataClientMock, timeout(TIMEOUT_MS)).saveFullIndex(any(), any());
    }

    @AfterEach
    void tearDown() {
        if (annaClient != null && annaClient.serverConnection != null) {
            annaClient.serverConnection.disconnect();
        }
        if (bobClient != null && bobClient.serverConnection != null) {
            bobClient.serverConnection.disconnect();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void quandAnnaPartageUneVideo_bobRecoitLaNotification_etLeServeurMetAJourLIndex() {
        VideoLight sharedVideo = new VideoLight(annaLight, "Anna - shared video", "Anna", Set.of("share", "integration"));

        annaClient.serverConnection.sendMessage(new ShareVideoMessage(sharedVideo));

        ArgumentCaptor<VideoLight> serverCaptor = ArgumentCaptor.forClass(VideoLight.class);
        verify(dataServerMock, timeout(TIMEOUT_MS)).updateIndex(serverCaptor.capture());
        assertNotNull(serverCaptor.getValue());
        assertEquals(sharedVideo.getVideoId(), serverCaptor.getValue().getVideoId());

        ArgumentCaptor<VideoLight> bobCaptor = ArgumentCaptor.forClass(VideoLight.class);
        verify(bobDataClientMock, timeout(TIMEOUT_MS)).addRemoteVideo(bobCaptor.capture());
        assertNotNull(bobCaptor.getValue());
        assertEquals(sharedVideo.getVideoId(), bobCaptor.getValue().getVideoId());

        verify(annaDataClientMock, after(TIMEOUT_MS).never()).addRemoteVideo(any());
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
