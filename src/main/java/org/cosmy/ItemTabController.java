package org.cosmy;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;

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
    private AnchorPane progressPane;
    @FXML
    private TextArea itemTextArea;

    public ItemTabController(CosmosContainer container) {
        this.container = container;
        this.tabName = generateTabName(container);
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
    }

}
