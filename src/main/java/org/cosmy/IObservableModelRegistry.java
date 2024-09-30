package org.cosmy;

import javafx.beans.Observable;
import org.cosmy.model.ObservableModelKey;

public interface IObservableModelRegistry {
    void register(ObservableModelKey key, Observable model);
    Observable lookup(ObservableModelKey key);
}
