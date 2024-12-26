package org.cosmy.controllers.itemtab;

import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.adapter.JavaBeanBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.context.PaginationContext;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.IController;
import org.cosmy.ui.CosmosItem;
import org.cosmy.ui.predicates.MouseDoubleClickEvent;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Optional;

public class ItemTabController implements IController {
    private final CosmosContainer container;
    private final String tabName;
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
    private Button clearFilterButton;
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
    @FXML
    private TextField filterQuery;

    private PaginationContext paginationContext;
    private ObjectMapper jsonPrinter;
    private JavaBeanBooleanProperty nextButtonBinding;

    //sub-controllers
    private ItemViewPaneController viewPaneController;
    private QueryFilterController filterController;
    private String partitionKey;

    public ItemTabController(CosmosContainer container) {
        this.container = container;
        this.tabName = generateTabName(container);
        this.jsonPrinter = JsonPrinterFactory.getJsonPrinter();
        this.paginationContext = new PaginationContext();
    }

    private String generateTabName(CosmosContainer container) {
        return container.getName().concat(container.getParent().getName()).concat("@").concat(container.getParent().getParent().getName());
    }

    @FXML
    public void initialize() {
        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);

        splitPane.widthProperty().addListener((observableValue, number, t1) -> {
            splitPane.setDividerPosition(0, 0.30);
        });

        //initialize sub-controllers
        viewPaneController = new ItemViewPaneController(this, container, itemTextArea, newItemButton, saveItemButton, deleteItemButton, validateItemButton, editItemButton);
        viewPaneController.initialize();
        filterController = new QueryFilterController(this, container, filterQuery, reloadItemsButton, clearFilterButton);
        filterController.initialize();

        //set action handlers for buttons
        prevPageButton.setOnAction(event -> {
            String prevContinuationToken = paginationContext.getPrevContinuationToken();
            if (prevContinuationToken != null && !prevContinuationToken.isEmpty()) {
                loadItems(Optional.of(prevContinuationToken));
                nextPageButton.setDisable(false);
            } else {
                loadItems(Optional.empty());
                prevPageButton.setDisable(true);
            }
        });

        nextPageButton.setOnAction(event -> {
            String continuationToken = paginationContext.getContinuationToken();
            if (continuationToken != null && !continuationToken.isEmpty()) {
                loadItems(Optional.of(continuationToken));
                nextPageButton.setDisable(false);
            } else {
                loadItems(Optional.empty());
                prevPageButton.setDisable(true);
            }
            loadItems(Optional.empty());
        });

        loadItems(Optional.empty());
    }

    private void clearPaginationContext() {
        this.paginationContext.clear();
    }

    public void removeItemFromListView(CosmosItem item) {
        Platform.runLater(() -> itemListView.getItems().remove(item));
    }

    public void loadItems(Optional<String> continuationToken) {
        SqlQuerySpec querySpec = filterController.getFilterQuery();
        CosmosPagedFlux<Map> pagedFlux = container.getAsyncContainer().queryItems(querySpec, Map.class);
        this.itemListView.getItems().clear();

        Flux<FeedResponse<Map>> responsePage = null;

        if (continuationToken.isEmpty()) {
            responsePage = pagedFlux.byPage(paginationContext.getPreferredPageLength());
        } else {
            responsePage = pagedFlux.byPage(continuationToken.get(), paginationContext.getPreferredPageLength());
        }

        responsePage.take(1).handle((page, synchronousSink) -> {
            page.getElements().stream().forEach(map -> {
                CosmosItem item = new CosmosItem(map.get(container.getPartitionKey()), (String) map.get("id"));
                item.addEventHandler(EventType.ROOT, event -> {
                    if (MouseDoubleClickEvent.evaluate(event)) {
                        CosmosItem source = (CosmosItem) event.getSource();
                        viewPaneController.loadItem(source, container);
                    }
                });
                Platform.runLater(() -> this.itemListView.getItems().add(item));
            });

            //set pagination details for next page
            if (page.getContinuationToken() == null || "".equalsIgnoreCase(page.getContinuationToken())) {
                System.out.println("continuation token is null !!");
//                paginationContext.setNextButtonDisabled(true);
                nextPageButton.setDisable(true);
            } else {
                paginationContext.setContinuationToken(page.getContinuationToken());
                paginationContext.setNextButtonDisabled(false);
                nextPageButton.setDisable(false);
                prevPageButton.setDisable(false);
            }

        }).doFinally(signalType -> {
            progressBar.setVisible(false);
        }).doOnError(throwable -> {
            System.out.println(throwable);
        }).subscribe();
        // TODO error handling
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
