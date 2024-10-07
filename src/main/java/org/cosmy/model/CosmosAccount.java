package org.cosmy.model;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CosmosAccount implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;
    private String accountHost;
    private String accountKey;
    private Map<String, CosmosDatabase> databases;
    private transient CosmosClient client;
    private transient CosmosAsyncClient asyncClient;
    private transient AtomicBoolean initialized = new AtomicBoolean(false);
    private transient boolean isAccountRefreshed = false;

    public CosmosAccount(String name, String connectionString, String accountKey) {
        this.name = name;
        this.accountHost = connectionString;
        this.accountKey = accountKey;
        this.databases = new HashMap<>();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        initialized = new AtomicBoolean(false);
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

    public boolean isAccountRefreshed() {
        return isAccountRefreshed;
    }

    public void setAccountRefreshed(boolean accountRefreshed) {
        isAccountRefreshed = accountRefreshed;
    }

    public void initialize() {
        if (!initialized.get()) {
            AzureKeyCredential credential = new AzureKeyCredential(this.getAccountKey());
            CosmosClientBuilder builder = new CosmosClientBuilder().credential(credential).endpoint(this.getAccountHost());
            client = builder.buildClient();
            asyncClient = builder.buildAsyncClient();
            initialized.set(true);
        }
    }

    public void refresh() {
        if (!initialized.get()) {
            initialize();
        }
        databases = new HashMap<>();
        client.readAllDatabases().iterator().forEachRemaining((dbProp) -> {
            CosmosDatabase dbInstance = new CosmosDatabase(dbProp.getId(), this);
            databases.put(dbProp.getId(), dbInstance);
            dbInstance.refresh();
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

}
