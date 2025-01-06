package org.cosmy.controllers.itemtab;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Button;
import org.cosmy.model.CosmosContainer;
import org.cosmy.spec.IController;
import org.cosmy.ui.CosmosItem;
import org.cosmy.utils.CosmosItemAttributes;
import org.cosmy.view.JsonTextArea;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ItemViewPaneController implements IController {
    private ItemTabController parentController;
    private CosmosContainer container;
    private final JsonTextArea itemTextArea;
    private final Button newItemButton;
    private final Button saveItemButton;
    private final Button deleteItemButton;
    private final Button validateItemButton;
    private final Button editItemButton;

    private final ObjectMapper jsonPrinter;

    public ItemViewPaneController(ItemTabController parentController, CosmosContainer container, JsonTextArea itemTextArea, Button newItemButton, Button saveItemButton, Button deleteItemButton, Button validateItemButton, Button editItemButton) {
        this.parentController = parentController;
        this.container = container;
        this.itemTextArea = itemTextArea;
        this.newItemButton = newItemButton;
        this.saveItemButton = saveItemButton;
        this.deleteItemButton = deleteItemButton;
        this.validateItemButton = validateItemButton;
        this.editItemButton = editItemButton;

        this.jsonPrinter = JsonPrinterFactory.getJsonPrinter();
    }

    @Override
    public void initialize() {
        itemTextArea.textProperty().addListener((observableValue, oldVal, newVal) -> {
            if (validateItemButton.isDisabled()) {
                this.validateItemButton.setDisable(false);
            }
        });
        deleteItemButton.setOnAction(event -> deleteItem());
        newItemButton.setOnAction(event -> newItem());
        validateItemButton.setOnAction(event -> validateNewItemJson());
        saveItemButton.setOnAction(event -> saveItem());
        editItemButton.setOnAction(event -> editItem());
    }

    private void deleteItem() {
        if (((Optional) this.deleteItemButton.getUserData()).isEmpty()) {
            return;
        }
        CosmosItem item = (CosmosItem) ((Optional) this.deleteItemButton.getUserData()).get();
        container.getAsyncContainer().deleteItem(item.getItemId(), new PartitionKey(item.getPartitionKey())).doOnSuccess(objectCosmosItemResponse -> {
            clearItemReadingPaneOnPlatformThread();
            parentController.removeItemFromListView(item);
            disableDeleteButtonOnPlatformThread();
        }).doOnError(throwable -> {
            //TODO error dialog
            System.out.println(throwable);
        }).subscribe();
    }

    private void clearItemReadingPaneOnPlatformThread() {
        Platform.runLater(this::clearItemReadingPane);
    }

    private void clearItemReadingPane() {
        itemTextArea.clear();
    }

    private void disableDeleteButtonOnPlatformThread() {
        Platform.runLater(this::disableDeleteButton);
    }

    private void disableDeleteButton() {
        this.deleteItemButton.setDisable(true);
        this.deleteItemButton.setUserData(Optional.ofNullable(null));
    }

    private void newItem() {
        try {
            String template = getNewItemTemplate();
            //TODO combine all in one runnable for platform thread
//            Platform.runLater(() -> {
            clearItemReadingPane();
            disableDeleteButton();
            this.itemTextArea.setText(template);
            this.itemTextArea.setEditable(true);
            this.validateItemButton.setDisable(true);
//            });
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

    private void validateNewItemJson() {
        String input = this.itemTextArea.getText();
        if (validateJson(input)) {
            Platform.runLater(() -> this.saveItemButton.setDisable(false));
        } else {
            parentController.showErrorDialog("Json is not valid");
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

    private void editItem() {
        itemTextArea.setEditable(true);
    }

    private void saveItem() {
        String itemText = this.itemTextArea.getText();
        try (Reader reader = new StringReader(itemText)) {
            JsonNode jsonNode = jsonPrinter.readTree(reader);
            String pKey = extractPartitionKey(jsonNode);
            Mono<CosmosItemResponse<JsonNode>> response;
            if (isNewItem(jsonNode)) {
                response = container.getAsyncContainer().createItem(jsonNode, new PartitionKey(pKey), new CosmosItemRequestOptions());
            } else {
                Optional<String> id = extractId(jsonNode);
                response = container.getAsyncContainer().replaceItem(jsonNode, id.get(), new PartitionKey(pKey));
            }

            response.handle((createResponse, synchronousSink) -> {
                //TODO handle diagnostic information if enabled through preferences
                System.out.println(createResponse.getStatusCode());
            }).doOnSuccess(object -> {
                //TODO success dialig or status bar/activity pane message
            }).doOnError(throwable -> {
                Platform.runLater(() -> parentController.showErrorDialog(throwable.getMessage()));
            }).subscribe();
        } catch (IOException e) {
            parentController.showErrorDialog(e.getMessage());
        }
    }

    private String extractPartitionKey(JsonNode jsonNode) {
        return jsonNode.get(container.getPartitionKey()).textValue();
    }

    private Optional<String> extractId(JsonNode jsonNode) {
        JsonNode idNode = jsonNode.get(CosmosItemAttributes.ID);
        if (idNode != null && !idNode.textValue().isEmpty()) {
            return Optional.of(idNode.textValue());
        }
        return Optional.empty();
    }

    private boolean isNewItem(JsonNode jsonNode) {
        JsonNode etagNode = jsonNode.get(CosmosItemAttributes.ETAG);
        return etagNode == null || etagNode.textValue() == null || "".equalsIgnoreCase(etagNode.textValue());
    }

    public void loadItem(CosmosItem item, CosmosContainer container) {
        // TODO error handling
        CosmosAsyncContainer asyncContainer = container.getAsyncContainer();

        // TODO support mutli-attribute partition keys
        asyncContainer.readItem(item.getItemId(), new PartitionKey(item.getPartitionKey()), Map.class).handle((response, synchronousSink1) -> {
            try {
                String asString = jsonPrinter.writeValueAsString(response.getItem());
                Platform.runLater(() -> {
                    try {
                        itemTextArea.setText(asString);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                        //TODO handle excepiton and route to error dialog
                    }
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

}
