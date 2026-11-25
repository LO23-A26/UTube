package client.communication;

/**
 * Launcher for starting the communication client. It serves as a entry point to test the communication module,
 * but also as an example of what a global launcher should look like.
 */
public class Launcher {
    
    // main
    public static void main(String[] args) {
        CommClientController clientController = new CommClientController();

        // obtenir les interfaces externes
        clientController.getDataCallsComm();
        clientController.getIhmShareCallsComm();
        clientController.getIhmMainCallsComm();

        // set les interfaces internes
        // clientController.setInterfaceCommIHMShare();
        // ...

        // To test, we should use a mocking framework to implement the external interfaces
        // So for now it just fails
        //clientController.run();
    }
}
