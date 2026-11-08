package client.communication;

//manual entry point to launch the communication client with mocked modules

public class MockLauncher {

    public static void main(String[] args) {
        CommClientTestFixture fixture = new CommClientTestFixture();
        fixture.controller.run();
    }
}