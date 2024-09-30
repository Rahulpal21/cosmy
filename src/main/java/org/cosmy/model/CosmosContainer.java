package org.cosmy.model;

import com.azure.cosmos.CosmosAsyncClient;
import javafx.scene.control.TreeItem;

import java.io.Serializable;

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

    public void refresh(CosmosAsyncClient client) {

    }

    public TreeItem<String> generateView(){
        TreeItem<String> item = new TreeItem<>(this.name);
        return item;
    }
}

