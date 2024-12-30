module org.cosmy {
    requires javafx.fxml;
    requires javafx.controls;
    requires com.azure.cosmos;
    requires jdk.compiler;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.material2;
    requires org.kordamp.ikonli.antdesignicons;
    requires org.kordamp.ikonli.remixicon;
    requires cosmos.paginator;
    requires org.jetbrains.annotations;

    opens org.cosmy to javafx.fxml;
    exports org.cosmy;
    exports org.cosmy.model;
    opens org.cosmy.model to javafx.fxml;
    exports org.cosmy.ui;
    opens org.cosmy.ui to javafx.fxml;
    exports org.cosmy.controllers;
    opens org.cosmy.controllers to javafx.fxml;
    exports org.cosmy.context;
    opens org.cosmy.context to javafx.fxml;
    exports org.cosmy.controllers.itemtab;
    opens org.cosmy.controllers.itemtab to javafx.fxml;
}
