package org.cosmy;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.cosmy.model.ObservableModelKey;

import java.io.IOException;

public class ItemsHandler {
    public void handle(String container, String database, String account) {
        Parent itemsTabPane = null;
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource( "itemTab.fxml"));
            loader.setController(new ItemTabController(ConnectionsContainer.getInstance().getConnection(account).getDatabase(database).getContainer(container)));
            itemsTabPane = loader.load();

        } catch (IOException e) {
            System.out.println(e);
        }
        ObservableList<Tab> tabs = ((TabPane) itemsTabPane).getTabs();
        Tab first = tabs.getFirst();
        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabsList = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
        tabsList.add(first);

//        ITab tab = new ItemsTab(ConnectionsContainer.getInstance().getConnection(account).getDatabase(database).getContainer(container));
//        tab.initialize();
    }
}
