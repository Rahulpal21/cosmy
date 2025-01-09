package org.cosmy.controllers.itemtab;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.IController;

public class QueryTabController implements IController {
    private final CosmosContainer container;
    private final String tabName;

    public QueryTabController(CosmosContainer container) {
        this.container = container;
        this.tabName = generateTabName(container);
    }

    private String generateTabName(CosmosContainer container) {
        return container.getName().concat(container.getParent().getName()).concat("@").concat(container.getParent().getParent().getName());
    }

    @FXML
    public void initialize() {
        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
    }

}
