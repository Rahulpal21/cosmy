package org.cosmy.state;

import java.io.IOException;

public interface IPersistedStateManager {
    void persist(Object object);
    Object load(Class type) throws IOException, ClassNotFoundException;
    void reload(Object object);
    void refresh(Object object);
}
