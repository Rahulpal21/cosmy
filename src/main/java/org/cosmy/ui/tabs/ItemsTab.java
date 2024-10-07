package org.cosmy.ui.tabs;

import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.cosmy.IObservableModelRegistry;
import org.cosmy.ObservableModelRegistryImpl;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.ui.predicates.MouseDoubleClickEvent;

import java.util.Map;

public class ItemsTab implements ITab {
    private final String tabName;
    private final CosmosContainer container;
    private ObservableList<Label> observableItems;
    private final StackPane stackPane;
    private final ObservableList<Node> observableStackPaneNodes;
    private final AnchorPane progressPane;
    private ItemsTabContentPane itemTextArea;

    public ItemsTab(CosmosContainer container) {
        this.container = container;
        this.stackPane = new StackPane();
        this.observableStackPaneNodes = this.stackPane.getChildren();
        this.progressPane = new AnchorPane(new ProgressBar());

        tabName = generateTabName(container);
        //TODO error handling
    }

    private String generateTabName(CosmosContainer container) {
        return container.getName().concat(container.getParent().getName()).concat("@").concat(container.getParent().getParent().getName());
    }

    @Override
    public void initialize() {

        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
        Tab tab = new Tab(container.getName());
        tab.setId(tabName);

        SplitPane splitPane = new SplitPane();

        splitPane.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.30);
        });

        ListView<Label> itemsListView = new ListView<>();
        observableItems = itemsListView.getItems();
        observableStackPaneNodes.add(itemsListView);
        observableStackPaneNodes.add(progressPane);
        splitPane.getItems().add(stackPane);

        itemTextArea = new ItemsTabContentPane("Select an item to view..");
        splitPane.getItems().add(itemTextArea);

        tab.setContent(splitPane);
        tabs.add(tab);
        loadItems();
    }

    public void loadItems() {
        String pKeyPath = container.getContainerDetails().getPartitionKeyPaths().getFirst();
        String pKeyAttr = pKeyPath.replace("/", "");
        String readAllQuery = "SELECT c.id, c."+pKeyAttr+" FROM c";
        SqlQuerySpec querySpec = new SqlQuerySpec(readAllQuery);
        CosmosPagedFlux<Map> pagedFlux = container.getAsyncContainer().queryItems(querySpec, Map.class);
        pagedFlux.handle((map, synchronousSink) -> {
            CosmosItem item = new CosmosItem(map.get(pKeyAttr) ,(String) map.get("id"));
            System.out.println(item.getItemId()+"  "+ item.getPartitionKey());
            item.addEventHandler(EventType.ROOT, event -> {
                if (MouseDoubleClickEvent.evaluate(event)) {
                    CosmosItem source = (CosmosItem) event.getSource();
                    itemTextArea.loadItem(source.getItemId(), source.getPartitionKey(), container);
                }
            });
            this.observableItems.add(item);
        }).doFinally(signalType -> {
            progressPane.setVisible(false);
        }).subscribe();
        // TODO error handling
    }

    public void dispose() {
        // TODO add closing action for tabs
    }
}
