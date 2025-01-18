package org.cosmy.view;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.controllers.QueryTabController;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.CosmyException;
import org.cosmy.spec.IController;
import org.cosmy.spec.IVisualElement;
import org.cosmy.utils.FXMLConstants;
import org.cosmy.utils.FXMLUtils;

import java.io.IOException;

///@author Rahul Pal
public class QueryTab implements IVisualElement {

    private final CosmosContainer container;
    private final IController controller;
    private AnchorPane queryPane;
    private Tab tab;

    public QueryTab(CosmosContainer container) {
        this.container = container;
        controller = new QueryTabController(container);
    }

    /**
     *
     */
    @Override
    public void initialize() {
        try {
            queryPane = (AnchorPane) FXMLUtils.loadFXML(FXMLConstants.QUERY_TAB_FXML, controller);
            tab = new Tab(container.getName(), queryPane);
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
