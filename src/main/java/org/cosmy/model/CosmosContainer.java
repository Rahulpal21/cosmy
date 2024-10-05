package org.cosmy.model;

import com.azure.cosmos.CosmosClient;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.TreeItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CosmosContainer implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;

    public CosmosContainer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void refresh(CosmosClient client) {

    }

    public TreeItem<String> generateView(){
        TreeItem<String> item = new TreeItem<>(this.name);
        item.getChildren().addAll(generateContainerOptions());
        return item;
    }

    private List<TreeItem<String>> generateContainerOptions() {
        List<TreeItem<String>> items = new ArrayList<>();
        TreeItem<String> documentsView = new TreeItem<>("Items");
        items.add(documentsView);
        return items;
    }

    private void loadDocumentsTab(EventTarget target) {
        System.out.println(target.getClass());
    }
}

