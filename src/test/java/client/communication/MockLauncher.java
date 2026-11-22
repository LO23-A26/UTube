package client.communication;

//manual entry point to launch the communication client with mocked modules

/**
 * 1. run server/main.java file
 * 2. run MockLauncher.main.java
 */


public class MockLauncher {

    public static void main(String[] args) {
        CommClientTestFixture fixture = new CommClientTestFixture();
        fixture.controller.run();
    }
}