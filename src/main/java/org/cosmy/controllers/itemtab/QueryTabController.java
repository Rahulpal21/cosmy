package org.cosmy.controllers.itemtab;

import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.cosmy.context.ConnectionsContainer;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.CosmosDatabase;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.IController;
import org.cosmy.view.QueryPaneContextBar;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class QueryTabController implements IController {
    private final CosmosContainer container;
    private final String tabName;

    @FXML
    private QueryPaneContextBar queryPaneToolbarLeft;
    @FXML
    private HBox queryPaneToolbarRight;
    @FXML
    private TextArea queryPaneEditor;
    @FXML
    private HBox resultsPaneToolbarLeft;
    @FXML
    private HBox resultsPaneToolbarRight;
    @FXML
    private TextArea resultsPane;

    public QueryTabController(CosmosContainer container) {
        this.container = container;
        this.tabName = generateTabName(container);
    }

    private String generateTabName(CosmosContainer container) {
        return container.getName().concat(container.getParent().getName()).concat("@").concat(container.getParent().getParent().getName());
    }

    @FXML
    public void initialize() {
        IObservableModelRegistry modelRegistry = ObservableModelRegistryImpl.getInstance();
        ObservableList<Tab> tabs = (ObservableList<Tab>) modelRegistry.lookup(ObservableModelKey.TABS);
        queryPaneToolbarLeft.initialize(extractDatabaseList(container), container);

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        queryPaneEditor.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyCombination.match(keyEvent)){
                System.out.println("CNTRL + ENTER  pressed");
            }
        });
    }

    private Set<CosmosDatabase> extractDatabaseList(CosmosContainer container) {
        Set<CosmosDatabase> dbs = new HashSet<>();
        dbs.add(container.getParent());
        return dbs;
    }

}
