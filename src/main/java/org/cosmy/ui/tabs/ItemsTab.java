package org.cosmy.ui.tabs;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.cosmy.ConnectionsContainer;
import org.cosmy.IObservableModelRegistry;
import org.cosmy.ObservableModelRegistryImpl;
import org.cosmy.model.CosmosAccount;
import org.cosmy.model.ObservableModelKey;

import java.util.Map;

public class ItemsTab implements ITab {
    private final String databaseName;
    private final String containerName;
    private String tabName;
    private ObservableList<String> observableItems;
    private ObservableList<CharSequence> observableContent;
    private final StackPane stackPane;
    private final String accountName;
    private final ObservableList<Node> observableStackPaneNodes;
    private final AnchorPane progressPane;

    public ItemsTab(String containerName, String databaseName, String accountName) {
        this.accountName = accountName;
        this.databaseName = databaseName;
        this.containerName = containerName;
        this.stackPane = new StackPane();
        this.observableStackPaneNodes = this.stackPane.getChildren();
        this.progressPane = new AnchorPane(new ProgressBar());

        tabName = containerName.concat(databaseName).concat("@").concat(accountName);
        //TODO error handling
        CosmosAccount account = ConnectionsContainer.getInstance().getConnection(accountName);
    }

    @Override
    public void initialize() {

        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
        Tab tab = new Tab(containerName);
        tab.setId(tabName);
        SplitPane splitPane = new SplitPane();

        splitPane.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.30);
        });

        ListView<String> itemsListView = new ListView<>();
        observableItems = itemsListView.getItems();
        observableStackPaneNodes.add(itemsListView);

        observableStackPaneNodes.add(progressPane);

        splitPane.getItems().add(stackPane);

        TextArea itemTextArea = new TextArea("Select an item to view..");
        observableContent = itemTextArea.getParagraphs();
        splitPane.getItems().add(itemTextArea);

        tab.setContent(splitPane);
        tabs.add(tab);
        loadItems();

    }

    public void loadItems() {
        CosmosAsyncClient asyncClient = ConnectionsContainer.getInstance().getConnection(accountName).getAsyncClient();
        CosmosAsyncContainer container = asyncClient.getDatabase(databaseName).getContainer(containerName);
        String readAllQuery = "SELECT c.id FROM c";
        SqlQuerySpec querySpec = new SqlQuerySpec(readAllQuery);
        CosmosPagedFlux<Map> pagedFlux = container.queryItems(querySpec, Map.class);
        pagedFlux.handle((map, synchronousSink) -> {
            this.observableItems.add((String) map.get("id"));
        }).doFinally(signalType -> {
            progressPane.setVisible(false);
        }).subscribe();
        // TODO error handling
    }

    public void dispose() {
        // TODO add closing action for tabs
    }
}
