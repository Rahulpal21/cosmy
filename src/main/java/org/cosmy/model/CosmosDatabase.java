package org.cosmy.model;

import com.azure.cosmos.CosmosAsyncDatabase;
import javafx.scene.control.TreeItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CosmosDatabase implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;
    private Map<String, CosmosContainer> collections;
    private transient CosmosAccount parent;
    private transient com.azure.cosmos.CosmosDatabase database;
    private transient CosmosAsyncDatabase asyncDatabase;
    private AtomicBoolean initialized = new AtomicBoolean(false);

    public CosmosDatabase(String name, CosmosAccount parent) {
        this.name = name;
        this.parent = parent;
        this.collections = new HashMap<>();
    }

    public CosmosDatabase addCollection(CosmosContainer collection) {
        collections.put(collection.getName(), collection);
        return this;
    }

    public CosmosAccount getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CosmosAsyncDatabase getAsyncDatabase() {
        return asyncDatabase;
    }

    public com.azure.cosmos.CosmosDatabase getDatabase() {
        return database;
    }

    public CosmosContainer getContainer(String containerName) {
        return collections.get(containerName);
    }

    public void refresh() {
        database = this.parent.getClient().getDatabase(name);
        asyncDatabase = this.parent.getAsyncClient().getDatabase(name);
        collections = new HashMap<>();
        database.readAllContainers().iterator().forEachRemaining((contProp) -> {
            CosmosContainer contInstance = new CosmosContainer(contProp.getId(), this);
            addCollection(contInstance);
            contInstance.refresh();
        });
        initialized.set(true);
    }

    public TreeItem<String> generateView() {
        TreeItem<String> item = new TreeItem<>(this.name);
        collections.forEach((s, cosmosContainer) -> {
            item.getChildren().add(cosmosContainer.generateView());
        });
        return item;
    }
}
