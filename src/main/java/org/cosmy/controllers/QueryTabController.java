package org.cosmy.controllers;

import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.util.CosmosPagedFlux;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.controllers.itemtab.JsonPrinterFactory;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.CosmosDatabase;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.IController;
import org.cosmy.utils.AppConstants;
import org.cosmy.view.DialogPopup;
import org.cosmy.view.JsonTextArea;
import org.cosmy.view.QueryPaneContextBar;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import reactor.core.publisher.Flux;

import java.util.*;

public class QueryTabController implements IController {
    private final CosmosContainer container;
    private final String tabName;

    @FXML
    private QueryPaneContextBar queryPaneToolbarLeft;
    @FXML
    private HBox queryPaneToolbarRight;
    @FXML
    private JsonTextArea queryPaneEditor;
    @FXML
    private HBox resultsPaneToolbarLeft;
    @FXML
    private HBox resultsPaneToolbarRight;
    @FXML
    private JsonTextArea resultsPane;

    private final ObjectMapper jsonPrinter;

    public QueryTabController(CosmosContainer container) {
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
        queryPaneToolbarLeft.initialize(extractDatabaseList(container), container);

        KeyCombination keyCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);

        queryPaneEditor.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyCombination.match(keyEvent)) {
                String query = extractRawQuery();
                submitQuery(query);
            }else if(KeyCode.ENTER.equals(keyEvent.getCode())){
                finalizeParagraph();
            }
        });
        queryPaneEditor.textProperty().addListener((observableValue, oldText, newText) -> {
//            System.out.println(oldText);
//            System.out.println(newText);
        });

    }

    private void finalizeParagraph() {
        String text = queryPaneEditor.getParagraph(queryPaneEditor.getCurrentParagraph() - 1).getText();
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        StringTokenizer tokenizer = new StringTokenizer(text, " ");
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            spansBuilder.add(Collections.singletonList(token), token.length());
        }
        StyleSpans<Collection<String>> styleSpans = spansBuilder.create();
        queryPaneEditor.setStyleSpans(0, styleSpans);
        Paragraph<Collection<String>, String, Collection<String>> paragraph = queryPaneEditor.getParagraph(queryPaneEditor.getCurrentParagraph());
        System.out.println(paragraph);

    }

    private void submitQuery(String query) {
        CosmosPagedFlux<Map> pagedFlux = container.getAsyncContainer().queryItems(query, Map.class);
        resultsPane.clear();
        Flux<FeedResponse<Map>> page = pagedFlux.byPage(AppConstants.RESULTS_PAGE_SIZE);
        page.handle((mapFeedResponse, synchronousSink) -> {
            mapFeedResponse.getElements().forEach(map -> {
                Platform.runLater(() -> {
                    try {
                        resultsPane.appendText(jsonPrinter.writeValueAsString(map) + "\n");
                    } catch (JsonProcessingException e) {
                        System.out.println(e);
                        //TODO log error on dialog or console
                    }
                });
            });
        }).doOnError(throwable -> {
            new DialogPopup(throwable.getMessage());
        }).subscribe();

    }

    private String extractRawQuery() {
        return queryPaneEditor.getText();
    }

    private Set<CosmosDatabase> extractDatabaseList(CosmosContainer container) {
        Set<CosmosDatabase> dbs = new HashSet<>();
        dbs.add(container.getParent());
        return dbs;
    }

}
