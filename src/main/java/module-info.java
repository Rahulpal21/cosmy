module org.cosmy.example {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.azure.cosmos;
    requires jdk.compiler;
    opens org.cosmy to javafx.fxml;
    exports org.cosmy;
    exports org.cosmy.model;
    opens org.cosmy.model to javafx.fxml;
}
