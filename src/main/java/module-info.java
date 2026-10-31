module utube {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop; //Temporary comment : added this line to remove errors in dataclasses

    opens client.ihmmain to javafx.fxml;
    exports client.main;
    exports client.ihmmain;
    exports common.interfaces.data; //Temporary comment : added this line to remove error import in IHMMainManager
    
}
