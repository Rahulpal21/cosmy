package org.cosmy;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosBulkOperationResponse;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.cosmy.model.CosmosContainer;
import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.UUID;

public class BulkImportController {
    @FXML
    public CheckBox generateId;

    @FXML
    private GridPane displayGrid;

    @FXML
    private ChoiceBox<String> accounts;

    @FXML
    private ChoiceBox<String> databases;

    @FXML
    private ChoiceBox<String> collections;

    private String selectedAccount;
    private String selectedDatabase;
    private String selectedCollection;
    private File selectedFile;

    @FXML
    private void initialize() {
        ConnectionsContainer.getInstance().iterateAccounts().forEachRemaining(cosmosAccount -> {
            accounts.getItems().add(cosmosAccount.getName());
        });
    }

    public void bulkImport(ActionEvent actionEvent) {
        if (inputsAreValid()) {
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getParent().getScene().getWindow();
            showProgress(stage);
            importItems();
        }
    }

    private void showProgress(Stage stage) {
        ProgressBar progressBar = new ProgressBar();
        Button button = new Button("Cancel");
        button.setCancelButton(true);
        button.setOnAction(actionEvent -> {
            stage.close();
        });
        displayGrid.getChildren().clear();
        displayGrid.add(progressBar, 1, 1, 2, 2);
        displayGrid.add(button, 3, 1, 1, 2);
    }

    private void importItems() {
        try (Reader reader = new BufferedReader(new FileReader(selectedFile))) {
            if (selectedFile == null) {
                throw new Exception("File is not selected.");
            }
            if (!selectedFile.canRead()) {
                throw new Exception("File is not readable.");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(reader);

            CosmosContainer container = ConnectionsContainer.getInstance().getConnection(selectedAccount).getDatabase(selectedDatabase).getContainer(selectedCollection);
            String partitionKey = container.getContainerDetails().getPartitionKeyPaths().getFirst().replace("/", "");
            CosmosAsyncContainer asyncContainer = container.getAsyncContainer();

            boolean idFlag = generateId.isSelected();

            Flux<CosmosItemOperation> operationsFlux = Flux.empty();
            if (jsonNode.isArray()) {
                operationsFlux = Flux.fromIterable(jsonNode).map(item -> {
                    return treatIds(item, idFlag);
                }).map(item -> CosmosBulkOperations.getCreateItemOperation(item, new PartitionKey(item.get(partitionKey).asText())));
            } else if (jsonNode.isObject()) {
                treatIds(jsonNode, idFlag);
                operationsFlux = Flux.just(CosmosBulkOperations.getCreateItemOperation(jsonNode, new PartitionKey(jsonNode.get(partitionKey).asText())));
            }

            Flux<CosmosBulkOperationResponse<Object>> responseFlux = asyncContainer.executeBulkOperations(operationsFlux);

            responseFlux.handle((operationResponse, synchronousSink) -> {
                inspectOperationResponse(operationResponse);
            }).doOnError(throwable -> {
                showErrorDialog(new StringBuffer(throwable.getMessage()));
            }).subscribe();

        } catch (Exception e) {
            showErrorDialog(new StringBuffer(e.getMessage()));
        }

    }

    private static JsonNode treatIds(JsonNode item, boolean idFlag) {
        return idFlag ? ((ObjectNode) item).set("id", new TextNode(UUID.randomUUID().toString())) : item;
    }

    private void inspectOperationResponse(CosmosBulkOperationResponse<Object> operationResponse) {
    }

    private boolean inputsAreValid() {
        StringBuffer errors = new StringBuffer();
        if (selectedFile == null) {
            errors.append("A valid file in not selected.\n");
        }
        if (selectedAccount == null) {
            errors.append("Account in not selected.\n");
        }
        if (selectedDatabase == null) {
            errors.append("Database is not selected.\n");
        }
        if (selectedCollection == null) {
            errors.append("Collection is not selected.\n");
        }

        if (!errors.isEmpty()) {
            showErrorDialog(errors);
            return false;
        }
        return true;
    }

    private void showErrorDialog(StringBuffer errors) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("errors..");
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(errors.toString());
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.showAndWait();
    }

    public void chooseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.txt", "*.json")
        );
        selectedFile = fileChooser.showOpenDialog(new Stage());
    }

    public void accountSelected(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof ChoiceBox) {
            selectedAccount = ((ChoiceBox<String>) actionEvent.getSource()).getValue();
            databases.getItems().clear();
            ConnectionsContainer.getInstance().getConnection(selectedAccount).iterateDatabases().forEachRemaining(cosmosDatabase -> {
                databases.getItems().add(cosmosDatabase.getName());
            });
        }
    }

    public void databaseSelected(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof ChoiceBox) {
            selectedDatabase = ((ChoiceBox<String>) actionEvent.getSource()).getValue();
            collections.getItems().clear();
            ConnectionsContainer.getInstance().getConnection(selectedAccount).getDatabase(selectedDatabase).iterateContainers().forEachRemaining(cosmosContainer -> {
                collections.getItems().add(cosmosContainer.getName());
            });
        }
    }

    public void collectionSelected(ActionEvent actionEvent) {
        if (actionEvent.getSource() instanceof ChoiceBox) {
            selectedCollection = ((ChoiceBox<String>) actionEvent.getSource()).getValue();
        }
    }

}
