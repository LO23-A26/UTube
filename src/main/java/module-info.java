module utube {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens client.ihmmain to javafx.fxml;
    exports client.main;
    exports client.ihmmain;
}
