package org.cosmy;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.ui.predicates.MouseDoubleClickEvent;
import org.cosmy.ui.CosmosItem;

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
    private ListView<CosmosItem> itemListView;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextArea itemTextArea;
    @FXML
    private VBox itemListVBox;
    @FXML
    private Button reloadItemsButton;
    @FXML
    private Button filterItemsButton;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private Button newItemButton;
    @FXML
    private Button saveItemButton;
    @FXML
    private Button deleteItemButton;

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

        //set action handlers for buttons
        deleteItemButton.setOnAction(event -> {
            deleteItem(deleteItemButton.getUserData());
        });
        reloadItemsButton.setOnAction(event -> {
            loadItems();
        });
        loadItems();
    }

    private void deleteItem(Object userData) {
        CosmosItem item = (CosmosItem) userData;
        container.getAsyncContainer().deleteItem(item.getItemId(), new PartitionKey(item.getPartitionKey())).doOnSuccess(objectCosmosItemResponse -> {
            clearItemReadingPane();
            removeItemFromListView(item);
            disableDeleteButton();
        }).doOnError(throwable -> {
            //TODO error dialog
            System.out.println(throwable);
        }).subscribe();
    }

    private void disableDeleteButton() {
        Platform.runLater(() -> this.deleteItemButton.setDisable(true));
    }

    private void removeItemFromListView(CosmosItem item) {
        Platform.runLater(() -> itemListView.getItems().remove(item));
    }

    private void clearItemReadingPane() {
        Platform.runLater(() -> itemTextArea.clear());
    }

    public void loadItems() {
        String pKeyPath = container.getContainerDetails().getPartitionKeyPaths().getFirst();
        String pKeyAttr = pKeyPath.replace("/", "");
        String readAllQuery = "SELECT c.id, c." + pKeyAttr + " FROM c";
        SqlQuerySpec querySpec = new SqlQuerySpec(readAllQuery);
        CosmosPagedFlux<Map> pagedFlux = container.getAsyncContainer().queryItems(querySpec, Map.class);
        this.itemListView.getItems().clear();
        pagedFlux.handle((map, synchronousSink) -> {
            CosmosItem item = new CosmosItem(map.get(pKeyAttr), (String) map.get("id"));
            item.addEventHandler(EventType.ROOT, event -> {
                if (MouseDoubleClickEvent.evaluate(event)) {
                    CosmosItem source = (CosmosItem) event.getSource();
                    loadItem(source, container);
                }
            });
            Platform.runLater(() -> this.itemListView.getItems().add(item));

        }).doFinally(signalType -> {
            progressBar.setVisible(false);
        }).doOnError(throwable -> {
            System.out.println(throwable);
        }).subscribe();
        // TODO error handling
    }

    public void loadItem(CosmosItem item, CosmosContainer container) {
        // TODO error handling
        CosmosAsyncContainer asyncContainer = container.getAsyncContainer();

        // TODO support mutli-attribute partition keys
        asyncContainer.readItem(item.getItemId(), new PartitionKey(item.getPartitionKey()), Map.class).handle((response, synchronousSink1) -> {
            try {
                String asString = jsonPrinter.writeValueAsString(response.getItem());
                Platform.runLater(() -> itemTextArea.setText(asString));
                enableDeleteButton();
                // TODO
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
                //TODO error handling
            }
        }).doOnError(throwable -> {
            System.out.println(throwable);
        }).doOnSuccess(object -> {
            contextualizeButtons(item);
        }).subscribe();

    }

    private void enableDeleteButton() {
        Platform.runLater(() -> this.deleteItemButton.setDisable(false));
    }

    private void contextualizeButtons(CosmosItem item) {
        this.deleteItemButton.setUserData(item);
    }

}
