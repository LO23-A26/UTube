module utube {
    requires javafx.controls;
    requires javafx.fxml;

    opens client.ihmmain to javafx.fxml;
    exports client.main;
    exports client.ihmmain;
}
