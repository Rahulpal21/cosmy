package org.cosmy.model;

import com.azure.cosmos.CosmosClient;
import javafx.scene.control.TreeItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CosmosDatabase implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;
    private Map<String, CosmosContainer> collections;

    public CosmosDatabase(String name) {
        this.name = name;
        this.collections = new HashMap<>();
    }
    public CosmosDatabase addCollection(CosmosContainer collection){
        collections.put(collection.getName(), collection);
        return this;
    }

    public CosmosContainer getCollection(String collectionName){
        return collections.get(collectionName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void refresh(CosmosClient client) {
        collections = new HashMap<>();
        client.getDatabase(this.name).readAllContainers().iterator().forEachRemaining((contProp) -> {
            CosmosContainer contInstance = new CosmosContainer(contProp.getId());
            contInstance.refresh(client);
            collections.put(contInstance.getName(), contInstance);
        });
    }

    public TreeItem<String> generateView(){
        TreeItem<String> item = new TreeItem<>(this.name);
        collections.forEach((s, cosmosContainer) -> {
            item.getChildren().add(cosmosContainer.generateView());
        });
        return item;
    }
}
