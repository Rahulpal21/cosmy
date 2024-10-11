package org.cosmy;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.ui.predicates.MouseDoubleClickEvent;
import org.cosmy.ui.tabs.CosmosItem;

import java.util.Map;

public class ItemTabController {
    private final CosmosContainer container;
    private final String tabName;
    //set name
    //set id
    @FXML
    private SplitPane splitPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private ListView<Label> itemListView;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea itemTextArea;
    @FXML
    private VBox itemListVBox;

    private ObjectMapper jsonPrinter;

    public ItemTabController(CosmosContainer container) {
        this.container = container;
        this.tabName = generateTabName(container);

        this.jsonPrinter = new ObjectMapper();
        this.jsonPrinter.registerModule(new JavaTimeModule());
        this.jsonPrinter.enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
    }

    private String generateTabName(CosmosContainer container) {
        return container.getName().concat(container.getParent().getName()).concat("@").concat(container.getParent().getParent().getName());
    }

    @FXML
    private void initialize() {
        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);

        splitPane.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.30);
        });

        itemListVBox.setPrefWidth(Double.MAX_VALUE);

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
                    loadItem(source.getItemId(), source.getPartitionKey(), container);
                }
            });
            this.itemListView.getItems().add(item);
        }).doFinally(signalType -> {
            progressBar.setVisible(false);
        }).doOnError(throwable -> {
            System.out.println(throwable);
        }).subscribe();
        // TODO error handling
    }

    public void loadItem(String id, Object partitionKey, CosmosContainer container) {
        // TODO error handling
        CosmosAsyncContainer asyncContainer = container.getAsyncContainer();

        // TODO support mutli-attribute partition keys

        asyncContainer.readItem(id, new PartitionKey(partitionKey), Map.class).handle((response, synchronousSink1) -> {
            try {
                itemTextArea.setText(jsonPrinter.writeValueAsString(response.getItem()));
                // TODO
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
                //TODO error handling
            }
        }).doOnError(throwable -> {
            System.out.println(throwable);
        }).subscribe();
    }
}
