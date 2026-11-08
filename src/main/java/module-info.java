module utube {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    opens client.ihmmain to javafx.fxml;
    opens client.ihmshare to javafx.fxml;
    exports client.main;
    exports client.ihmmain;
    exports client.ihmshare;
    exports common.dataclasses;
    exports common.interfaces.data; //Temporary comment : added this line to remove error import in IHMMainManager
    exports common.interfaces.IHMShare;
    exports common.interfaces.comm;
}
