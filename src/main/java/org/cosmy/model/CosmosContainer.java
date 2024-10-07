package org.cosmy.model;

import com.azure.cosmos.CosmosAsyncContainer;
import javafx.event.EventTarget;
import javafx.scene.control.TreeItem;
import org.cosmy.ui.ContainerDetailsFlyweight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CosmosContainer implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;
    private transient com.azure.cosmos.CosmosContainer container;
    private transient CosmosAsyncContainer asyncContainer;
    private transient AtomicBoolean initialized = new AtomicBoolean(false);
    private transient CosmosDatabase parent;
    private transient ContainerDetailsFlyweight containerDetails;

    public CosmosContainer(String name, CosmosDatabase parent) {
        this.name = name;
        this.parent = parent;
    }

    public CosmosDatabase getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void refresh() {
        container = this.parent.getDatabase().getContainer(this.getName());
        asyncContainer = this.parent.getAsyncDatabase().getContainer(this.getName());
        this.containerDetails = new ContainerDetailsFlyweight(this.name, this.parent.getName(), this.parent.getParent().getName());
        this.containerDetails.setPartitionKeyPaths(new ArrayList<>(container.read().getProperties().getPartitionKeyDefinition().getPaths()));
        initialized.set(true);
    }

    public TreeItem<String> generateView() {
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

