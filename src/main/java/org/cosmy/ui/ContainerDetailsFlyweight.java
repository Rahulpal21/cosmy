package org.cosmy.ui;

import java.util.List;

public class ContainerDetailsFlyweight {
    private String containerName;
    private String databaseName;
    private String accountName;
    private List<String> partitionKeyPaths;

    public ContainerDetailsFlyweight(String containerName, String databaseName, String accountName) {
        this.containerName = containerName;
        this.databaseName = databaseName;
        this.accountName = accountName;
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
