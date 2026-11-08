package client.communication;

import static org.mockito.Mockito.mock;

import common.interfaces.IHMMain.InterfaceCommCallsIHMMain;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;
import common.interfaces.data.InterfaceCommClientCallsDataClient;


// Builds a CommClientController whose external dependencies (Data, IHM-Share, IHM-Main) are replaced by Mockito mocks
// Only the interfaces that Communication calls are mocked

public class CommClientTestFixture {

    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 9000;

    public final CommClientController controller;

    // Fake versions of the other modules
    public final InterfaceCommClientCallsDataClient dataMock;
    public final InterfaceCommunicationCallsIHMShare ihmShareMock;
    public final InterfaceCommCallsIHMMain ihmMainMock;

    // Creates the controller and injects every mock that the controller can currently receive
    public CommClientTestFixture() {
        this.dataMock = mock(InterfaceCommClientCallsDataClient.class);
        this.ihmShareMock = mock(InterfaceCommunicationCallsIHMShare.class);
        this.ihmMainMock = mock(InterfaceCommCallsIHMMain.class);

        this.controller = new CommClientController();
        this.controller.setInterfaceCommDataClient(dataMock);
        this.controller.setInterfaceCommIHMShare(ihmShareMock);
        // TODO: inject ihmMainMock once CommClientController has a setter for InterfaceCommCallsIHMMain
    }
}