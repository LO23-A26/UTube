package server.main;

import server.communication.CommServerController;
import server.data.DataServerCore;

public class UTubeServerLauncher {

    public void main(String[] args) {
        CommServerController comm_controller = new CommServerController();
        DataServerCore data_controller = new DataServerCore();
        comm_controller.setInterfaceCommServerCallsDataServer(data_controller.getInterfaceForCS());
        comm_controller.start();
    }
}
