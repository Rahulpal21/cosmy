package org.cosmy;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.cosmy.ui.CosmosItem;
import org.cosmy.ui.predicates.MouseDoubleClickEvent;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
    @FXML
    private Button validateItemButton;
    @FXML
    private Button editItemButton;

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

        itemTextArea.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (validateItemButton.isDisabled()) {
                this.validateItemButton.setDisable(false);
            }
        });
        //set action handlers for buttons
        deleteItemButton.setOnAction(event -> deleteItem());
        reloadItemsButton.setOnAction(event -> loadItems());
        newItemButton.setOnAction(event -> newItem());
        validateItemButton.setOnAction(event -> validateNewItemJson());
        saveItemButton.setOnAction(event -> saveItem());
        editItemButton.setOnAction(event -> editItem());
        loadItems();
    }

    private void editItem() {
        itemTextArea.setEditable(true);
    }

    private void saveItem() {
        String itemText = this.itemTextArea.getText();
        try(Reader reader = new StringReader(itemText)){
            JsonNode jsonNode = jsonPrinter.readTree(reader);
            Mono<CosmosItemResponse<JsonNode>> response = container.getAsyncContainer().upsertItem(jsonNode);
            response.handle((createResponse, synchronousSink) -> {
                //TODO handle diagnostic information if enabled through preferences
                System.out.println(createResponse.getStatusCode());
            }).doOnSuccess(object -> {
                //TODO success dialig or status bar/activity pane message
            }).doOnError(throwable -> {
                showErrorDialog(throwable.getMessage());
            }).subscribe();
        } catch (IOException e) {
            showErrorDialog(e.getMessage());
        }

    }

    private void validateNewItemJson() {
        String input = this.itemTextArea.getText();
        if (validateJson(input)) {
            Platform.runLater(() -> this.saveItemButton.setDisable(false));
        } else {
            showErrorDialog("Json is not valid");
        }
    }

    private boolean validateJson(String input) {
        //TODO return error details
        try (Reader stringReader = new StringReader(input)) {
            this.jsonPrinter.readTree(stringReader);
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void newItem() {
        try {
            String template = getNewItemTemplate();
            //TODO combine all in one runnable for platform thread
            Platform.runLater(() -> {
                clearItemReadingPane();
                disableDeleteButton();
                this.itemTextArea.setText(template);
                this.itemTextArea.setEditable(true);
                this.validateItemButton.setDisable(true);
            });


        } catch (JsonProcessingException e) {
            //TODO error dialog
            System.out.println(e);
        }
    }

    private String getNewItemTemplate() throws JsonProcessingException {
        Map<String, String> itemTemplateAttributes = new HashMap<>(2);
        itemTemplateAttributes.put("id", UUID.randomUUID().toString());
        itemTemplateAttributes.put(container.getContainerDetails().getPartitionKeyPaths().getFirst().replace("/", ""), "<replace with actual value>");
        return this.jsonPrinter.writeValueAsString(itemTemplateAttributes);
    }

    private void deleteItem() {
        if (((Optional) this.deleteItemButton.getUserData()).isEmpty()) {
            return;
        }
        CosmosItem item = (CosmosItem) ((Optional) this.deleteItemButton.getUserData()).get();
        container.getAsyncContainer().deleteItem(item.getItemId(), new PartitionKey(item.getPartitionKey())).doOnSuccess(objectCosmosItemResponse -> {
            clearItemReadingPaneOnPlatformThread();
            removeItemFromListViewOnPlatformThread(item);
            disableDeleteButtonOnPlatformThread();
        }).doOnError(throwable -> {
            //TODO error dialog
            System.out.println(throwable);
        }).subscribe();
    }

    private void disableDeleteButtonOnPlatformThread() {
        Platform.runLater(this::disableDeleteButton);
    }

    private void disableDeleteButton() {
        this.deleteItemButton.setDisable(true);
        this.deleteItemButton.setUserData(Optional.ofNullable(null));
    }

    private void removeItemFromListViewOnPlatformThread(CosmosItem item) {
        Platform.runLater(() -> itemListView.getItems().remove(item));
    }

    private void clearItemReadingPaneOnPlatformThread() {
        Platform.runLater(this::clearItemReadingPane);
    }

    private void clearItemReadingPane() {
        itemTextArea.clear();
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
                Platform.runLater(() -> {
                    itemTextArea.setText(asString);
                    itemTextArea.setEditable(false);
                    validateItemButton.setDisable(true);
                    saveItemButton.setDisable(true);
                    editItemButton.setDisable(false);
                });
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
        this.deleteItemButton.setUserData(Optional.ofNullable(item));
    }

    public void showErrorDialog(String errorMessage) {
        //TODO take error dialog to a common utility across whole project
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Error");
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(errorMessage);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.showAndWait();
    }

}
