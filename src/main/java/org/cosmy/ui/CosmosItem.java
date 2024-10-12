package org.cosmy.ui;

import javafx.scene.control.Label;

public class CosmosItem extends Label {
    private Object partitionKey;
    private String itemId;

    public CosmosItem(Object partitionKey, String itemId) {
        super(itemId);
        this.partitionKey = partitionKey;
        this.itemId = itemId;
    }

    public Object getPartitionKey() {
        return partitionKey;
    }

    public String getItemId() {
        return itemId;
    }
}
