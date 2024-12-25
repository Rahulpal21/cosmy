package org.cosmy.controllers.itemtab;

import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import org.cosmy.model.CosmosContainer;
import org.cosmy.spec.IController;
import org.cosmy.ui.CosmosItem;
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
    private final TextArea itemTextArea;
    private final Button newItemButton;
    private final Button saveItemButton;
    private final Button deleteItemButton;
    private final Button validateItemButton;
    private final Button editItemButton;

    private final ObjectMapper jsonPrinter;

    public ItemViewPaneController(ItemTabController parentController, CosmosContainer container, TextArea itemTextArea, Button newItemButton, Button saveItemButton, Button deleteItemButton, Button validateItemButton, Button editItemButton) {
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
            Mono<CosmosItemResponse<JsonNode>> response = container.getAsyncContainer().upsertItem(jsonNode);
            response.handle((createResponse, synchronousSink) -> {
                //TODO handle diagnostic information if enabled through preferences
                System.out.println(createResponse.getStatusCode());
            }).doOnSuccess(object -> {
                //TODO success dialig or status bar/activity pane message
            }).doOnError(throwable -> {
                parentController.showErrorDialog(throwable.getMessage());
            }).subscribe();
        } catch (IOException e) {
            parentController.showErrorDialog(e.getMessage());
        }
    }

}
