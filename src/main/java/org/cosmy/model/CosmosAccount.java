package org.cosmy.model;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CosmosAccount implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;
    private String accountHost;
    private String accountKey;
    private transient CosmosClient client;
    private transient CosmosAsyncClient asyncClient;
    private Map<String, CosmosDatabase> databases;
    private transient boolean isClientInitialized = false;
    private transient boolean isAccountRefreshed = false;

    public CosmosAccount(String name, String connectionString, String accountKey) {
        this.name = name;
        this.accountHost = connectionString;
        this.accountKey = accountKey;
        this.databases = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountHost() {
        return accountHost;
    }

    public void setAccountHost(String accountHost) {
        this.accountHost = accountHost;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }

    public CosmosAccount addDatabase(CosmosDatabase database) {
        this.databases.put(database.getName(), database);
        return this;
    }

    public CosmosDatabase getDatabase(String database) {
        return databases.get(database);
    }

    public CosmosClient getClient() {
        return client;
    }

    public void setClient(CosmosClient client) {
        this.client = client;
    }

    public boolean isClientInitialized() {
        return isClientInitialized;
    }

    public void setClientInitialized(boolean clientInitialized) {
        isClientInitialized = clientInitialized;
    }

    public boolean isAccountRefreshed() {
        return isAccountRefreshed;
    }

    public void setAccountRefreshed(boolean accountRefreshed) {
        isAccountRefreshed = accountRefreshed;
    }

    public void refresh() {
        databases = new HashMap<>();
        if (!isClientInitialized) {
            initializeClient();
        }
        client.readAllDatabases().iterator().forEachRemaining((dbProp) -> {
            CosmosDatabase dbInstance = new CosmosDatabase(dbProp.getId());
            dbInstance.refresh(client);
            databases.put(dbProp.getId(), dbInstance);
        });
    }

    public int databaseCount() {
        return databases.size();
    }

    public Iterator<CosmosDatabase> iterateDatabases() {
        return databases.values().iterator();
    }

    public CosmosAsyncClient getAsyncClient() {
        return asyncClient;
    }

    public void setAsyncClient(CosmosAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
    }

    public void initializeClient() {
        if (!isClientInitialized) {
            AzureKeyCredential credential = new AzureKeyCredential(this.getAccountKey());
            CosmosClientBuilder builder = new CosmosClientBuilder().credential(credential).endpoint(this.getAccountHost());
            CosmosClient client = builder.buildClient();
            client.readAllDatabases();
            setClient(client);
            CosmosAsyncClient asyncClient = builder.buildAsyncClient();
            asyncClient.readAllDatabases();
            setAsyncClient(asyncClient);
            setClientInitialized(true);
        }
    }
}
