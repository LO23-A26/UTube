package comm_integration_tests.mock_implementations;

import common.dataclasses.VideoFull;
import common.interfaces.IHMShare.InterfaceCommunicationCallsIHMShare;

public class MockCommunicationCallsIHMShare implements InterfaceCommunicationCallsIHMShare {

    @Override
    public void displayVideoDetails(VideoFull videoDetails) {
        System.out.println("[MockCommunicationCallsIHMShare] displayVideoDetails: " + videoDetails);
    }

    @Override
    public void downloadDone(String videoPath) {
        System.out.println("[MockCommunicationCallsIHMShare] downloadDone: " + videoPath);
    }
}
