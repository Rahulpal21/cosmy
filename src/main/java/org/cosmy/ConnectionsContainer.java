package org.cosmy;

import org.cosmy.model.CosmosAccount;
import org.cosmy.state.FilePersistedStateManager;
import org.cosmy.state.IPersistedStateManager;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsContainer {
    private static ConnectionsContainer instance;
    private Map<String, CosmosAccount> connections;
    private IPersistedStateManager stateManager;

    public static ConnectionsContainer getInstance() {
        if (instance == null) {
            synchronized (ConnectionsContainer.class) {
                if (instance == null) {
                    instance = new ConnectionsContainer();
                }
            }
        }
        return instance;
    }

    public ConnectionsContainer() {
        this.connections = new ConcurrentHashMap<>();
        this.stateManager = new FilePersistedStateManager();
        restore();
    }

    public void addConnection(CosmosAccount account) {
        connections.put(account.getName(), account);
    }

    public void persist(){
        stateManager.persist(connections);
    }

    public void restore(){
        try {
            connections = (Map<String, CosmosAccount>) stateManager.load(ConcurrentHashMap.class);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    public Iterator<CosmosAccount> iterateAccounts(){
        return connections.values().iterator();
    }

    public CosmosAccount getConnection(String name) {
        return connections.get(name);
    }
}
