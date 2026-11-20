package comm_integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import client.communication.CommClientController;
import common.dataclasses.Index;
import common.dataclasses.UserFull;
import common.dataclasses.UserLight;
import common.dataclasses.VideoLight;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.data.InterfaceCommClientCallsDataClient;
import common.interfaces.data.InterfaceCommServerCallsDataServer;
import server.communication.CommServerController;

class ClientConnectionTest {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int TIMEOUT_MS = 5000;

    private int port;
    private CommServerController server;
    private CommClientController client;

    private InterfaceCommServerCallsDataServer dataServerMock;
    private InterfaceCommClientCallsDataClient dataClientMock;

    private UserLight annaLight;
    private UserFull annaFull;
    private List<VideoLight> annaVideos;

    @BeforeEach
    void setUp() throws IOException {
        port = findFreePort();

        annaFull = new UserFull("Anna", "pwd", "Anna", null, null);
        annaLight = annaFull.getSharedInfo();
        annaVideos = new ArrayList<>(List.of(
                new VideoLight(annaLight, "Anna - video 1", "Anna", Set.of("test", "comm"))));

        // --- mocks des modules externes ---
        dataServerMock = mock(InterfaceCommServerCallsDataServer.class);
        dataClientMock = mock(InterfaceCommClientCallsDataClient.class);

        // le module data serveur répond avec un index contenant Anna et sa vidéo
        Index index = new Index(new ArrayList<>(List.of(annaLight)), new ArrayList<>(annaVideos));
        when(dataServerMock.registerUserAndGetIndexStructure(any(), any())).thenReturn(index);
        when(dataClientMock.getMyProfile()).thenReturn(annaFull);
        when(dataClientMock.askRequesterId()).thenReturn(annaLight.getUuid());

        // --- serveur ---
        server = new CommServerController();
        server.setInterfaceCommServerCallsDataServer(dataServerMock);
        server.start(port);

        // --- client ---
        client = new CommClientController();
        client.setInterfaceCommIHMShare(mock(InterfaceCommunicationCallsIHMShare.class));
        client.setInterfaceCommDataClient(dataClientMock);
    }

    @AfterEach
    void tearDown() {
        client.serverConnection.disconnect();
        server.stop();
    }

    @Test
    void clientConnecte_serverReceivesProfil_clientReceivesIndex() {
        client.getDataCallsComm().sendProfileInfo(SERVER_IP, port, annaVideos, "Anna");

        // 1. le serveur a transmis le profil et les vidéos à son module data
        ArgumentCaptor<UserLight> userCaptor = ArgumentCaptor.forClass(UserLight.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<VideoLight>> videosCaptor = ArgumentCaptor.forClass(Set.class);

        verify(dataServerMock, timeout(TIMEOUT_MS))
                .registerUserAndGetIndexStructure(userCaptor.capture(), videosCaptor.capture());

        assertNotNull(userCaptor.getValue());
        assertEquals(1, videosCaptor.getValue().size());

        // 2. le client a transmis à son module data l'index reçu du serveur
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserLight>> usersCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<VideoLight>> indexVideosCaptor = ArgumentCaptor.forClass(List.class);

        verify(dataClientMock, timeout(TIMEOUT_MS))
                .saveFullIndex(usersCaptor.capture(), indexVideosCaptor.capture());

        assertEquals(1, usersCaptor.getValue().size());
        assertEquals(annaLight.getUuid(), usersCaptor.getValue().get(0).getUuid());
        assertEquals(1, indexVideosCaptor.getValue().size());
    }

    /** Asks the OS for a free port (port 0), then releases it so the server can use it. */
    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
