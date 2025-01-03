package org.cosmy.view;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.controllers.itemtab.ItemTabController;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.CosmyException;
import org.cosmy.spec.IController;
import org.cosmy.spec.IVisualElement;
import org.cosmy.utils.FXMLConstants;
import org.cosmy.utils.FXMLUtils;

import java.io.IOException;

///@author Rahul Pal
public class ItemsTab implements IVisualElement {

    private final CosmosContainer container;
    private final IController controller;
    private Tab tab;

    public ItemsTab(CosmosContainer container) {
        this.container = container;
        controller = new ItemTabController(container);

    }

    /**
     *
     */
    @Override
    public void initialize() {
        try {
            tab = FXMLUtils.loadFXMLTab(FXMLConstants.ITEMS_TAB_FXML, controller);
            tab.setText(container.getName());
            IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
            ObservableList<Tab> tabsList = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
            tabsList.add(tab);
            tab.getTabPane().getSelectionModel().select(tab);
        } catch (IOException e) {
            throw new CosmyException(e.getMessage(), e);
        }
    }

    /**
     *
     */
    @Override
    public void reset() {

    }

    /**
     *
     */
    @Override
    public void registerActionListener() {

    }

    public Tab getTab() {
        return tab;
    }
}
