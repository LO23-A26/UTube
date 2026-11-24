module utube {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens client.ihmmain to javafx.fxml;
    opens client.ihmshare to javafx.fxml;
    exports client.main;
    exports client.ihmshare;
    exports common.interfaces.IHMShare;
    exports server.main;
}
