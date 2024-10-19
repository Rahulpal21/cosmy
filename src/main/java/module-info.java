module org.cosmy.example {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.azure.cosmos;
    requires jdk.compiler;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;

    opens org.cosmy to javafx.fxml;
    exports org.cosmy;
    exports org.cosmy.model;
    opens org.cosmy.model to javafx.fxml;
    exports org.cosmy.ui;
    opens org.cosmy.ui to javafx.fxml;
}
