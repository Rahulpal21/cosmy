package org.cosmy;

import javafx.beans.Observable;
import org.cosmy.model.ObservableModelKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObservableModelRegistryImpl implements IObservableModelRegistry {
    private static IObservableModelRegistry instance;
    private static Map<ObservableModelKey, Observable> registry;

    public static IObservableModelRegistry getInstance(){
        if (instance == null) {
            synchronized (ObservableModelRegistryImpl.class) {
                if (instance == null) {
                    instance = new ObservableModelRegistryImpl();
                }
            }
        }
        return instance;
    }

    private ObservableModelRegistryImpl() {
        registry = new ConcurrentHashMap<>();
    }

    @Override
    public void register(ObservableModelKey key, Observable model) {
        registry.put(key, model);
    }

    @Override
    public Observable lookup(ObservableModelKey key) {
        return registry.get(key);
    }
}
