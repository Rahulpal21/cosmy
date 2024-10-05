package org.cosmy;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import org.cosmy.model.ObservableModelKey;

public class ItemsHandler {
    public void handle(String container, String database, String account) {
        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
        Tab tab = new Tab(container);
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().add(new ListView<>());
        splitPane.getItems().add(new TextArea("this is a text area"));
        tab.setContent(splitPane);
        tabs.add(tab);
    }
}
