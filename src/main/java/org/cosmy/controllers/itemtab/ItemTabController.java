package org.cosmy.controllers.itemtab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.rahulpal21.cosmospaginator.CosmosPaginable;
import com.gmail.rahulpal21.cosmospaginator.CosmosPaginationBuilder;
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
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.model.Preferences;
import org.cosmy.spec.IController;
import org.cosmy.ui.CosmosItem;
import org.cosmy.ui.predicates.MouseDoubleClickEvent;
import org.cosmy.view.JsonTextArea;

import java.util.Map;
import java.util.stream.Stream;

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
    private JsonTextArea itemTextArea;
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

    private CosmosPaginable<Map> paginationContext;
    private ObjectMapper jsonPrinter;
    private JavaBeanBooleanProperty nextButtonBinding;

    //sub-controllers
    private ItemViewPaneController viewPaneController;
    private QueryFilterController filterController;

    public ItemTabController(CosmosContainer container) {
        this.container = container;
        this.tabName = generateTabName(container);
        this.jsonPrinter = JsonPrinterFactory.getJsonPrinter();
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
            loadItems(paginationContext.prev());
        });

        nextPageButton.setOnAction(event -> {
            loadItems(paginationContext.next());
        });

        paginationContext = createPaginationContext();
        loadItems();
    }

    private CosmosPaginable<Map> createPaginationContext() {
        //initialize pagination context
        CosmosPaginationBuilder<Map> paginationBuilder = new CosmosPaginationBuilder<>();
        if(Preferences.getInstance().getPageLength()>=0){
            paginationBuilder.setPageSize(Preferences.getInstance().getPageLength());
        }
        return paginationBuilder.build(container.getContainer(), filterController.getFilterQuery(), Map.class);
    }

    public void removeItemFromListView(CosmosItem item) {
        Platform.runLater(() -> itemListView.getItems().remove(item));
    }

    public void loadItems() {
        paginationContext.init();
        if (paginationContext.hasNext()) {
            loadItems(paginationContext.next());
        }
    }

    private void refreshPaginationButtons() {
        nextPageButton.setDisable(!paginationContext.hasNext());
        prevPageButton.setDisable(!paginationContext.hasPrev());
    }

    public void loadItems(Stream<? super Map> itemStream) {
        this.itemListView.getItems().clear();

        itemStream.forEach(map -> {
            CosmosItem item = new CosmosItem(
                    ((Map) map).get(container.getPartitionKey()), (String) ((Map) map).get("id"));
            item.addEventHandler(EventType.ROOT, event -> {
                if (MouseDoubleClickEvent.evaluate(event)) {
                    CosmosItem source = (CosmosItem) event.getSource();
                    viewPaneController.loadItem(source, container);
                }
            });
            Platform.runLater(() -> {
                this.itemListView.getItems().add(item);
                //TODO take this out of inner loop
                this.progressBar.setVisible(false);
            });
        });

        refreshPaginationButtons();
    }

    public void showErrorDialog(String errorMessage) {
        //TODO take error dialog to a common utility across whole project
        Dialog<String> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.setHeight(180);
        dialog.setWidth(500);
        dialog.setTitle("Error");
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(errorMessage);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.showAndWait();
    }

    public void reloadItems() {
        paginationContext = createPaginationContext();
        loadItems();
    }
}
