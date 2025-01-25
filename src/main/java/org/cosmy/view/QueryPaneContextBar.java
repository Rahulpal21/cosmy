package org.cosmy.view;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.CosmosDatabase;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class QueryPaneContextBar extends HBox {
    private static final String DATABASE_LABEL = "Database";
    private static final String COLLECTION_LABEl = "Collection";
//    private ComboBox<String> database;
//    private ComboBox<String> collection;

    public QueryPaneContextBar() {
        super();
    }

    public void initialize(Set<CosmosDatabase> databases, CosmosContainer selectedContainer) {
        this.getChildren().add(new Label(DATABASE_LABEL.concat(": ".concat(selectedContainer.getParent().getName()).concat(", "))));
//        database = new ComboBox<>();
//        database.getItems().setAll(databases.stream().map(CosmosDatabase::getName).collect(Collectors.toSet()));
//        database.getSelectionModel().select(selectedContainer.getParent().getName());
//        this.getChildren().add(database);

        this.getChildren().add(new Label(COLLECTION_LABEl.concat(": ".concat(selectedContainer.getName()))));
//        collection = new ComboBox<>();
//        collection.getItems().setAll(extractContainerList(selectedContainer.getParent()));
//        collection.getSelectionModel().select(selectedContainer.getName());
//        this.getChildren().add(collection);
    }

    private Set<String> extractContainerList(CosmosDatabase parent) {
        HashSet<String> containers = new HashSet<>();
        parent.iterateContainers().forEachRemaining(container -> {
            containers.add(container.getName());
        });

        return containers;
    }
}
