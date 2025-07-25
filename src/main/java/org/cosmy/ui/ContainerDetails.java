package org.cosmy.ui;

import com.azure.cosmos.CosmosContainer;

import java.util.ArrayList;
import java.util.List;

public class ContainerDetails {
    private String containerName;
    private String databaseName;
    private String accountName;
    private List<String> partitionKeyPaths;

    public ContainerDetails(String containerName, String databaseName, String accountName) {
        this.containerName = containerName;
        this.databaseName = databaseName;
        this.accountName = accountName;
    }

    public ContainerDetails(CosmosContainer container) {
        setPartitionKeyPaths(new ArrayList<>(container.read().getProperties().getPartitionKeyDefinition().getPaths()));
    }

    public List<String> getPartitionKeyPaths() {
        return partitionKeyPaths;
    }

    public void setPartitionKeyPaths(List<String> partitionKeyPaths) {
        this.partitionKeyPaths = partitionKeyPaths;
    }

    public String getContainerName() {
        return containerName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getAccountName() {
        return accountName;
    }
}
