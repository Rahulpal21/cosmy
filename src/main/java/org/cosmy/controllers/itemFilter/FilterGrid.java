package org.cosmy.controllers.itemFilter;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class FilterGrid extends Pane implements IFilterGrid {
    private final AtomicInteger rowcount = new AtomicInteger(0);
    private final Function<List<Filter>, Integer> checkCallback;

    private VBox vBox = new VBox();
    private GridPane grid = new GridPane();
    private String[] operators;

    public FilterGrid(String[] operators, Function<List<Filter>, Integer> checkCallback) {
        super();
        super.getChildren().add(vBox);
        this.operators = operators;
        this.checkCallback = checkCallback;
        initializeGrid();
        initializeButtons();
    }

    private void initializeGrid() {
        grid.setHgap(5);
        AnchorPane gridContainer = new AnchorPane(grid);
        vBox.getChildren().add(gridContainer);
        grid.addRow(0, headerRow());
        grid.addRow(1, emptyRow(1));
        rowcount.incrementAndGet();
    }

    private Node[] emptyRow(int index) {

        TextField property = TextFields.createClearableTextField();

//        TextFields.bindAutoCompletion(property, new String[]{"c.id", "c.pkey"});
        ChoiceBox<String> condition = new ChoiceBox<>();
        condition.getItems().setAll(operators);
        TextField expression = new TextField();
        Button delete = new Button("Delete");
        Button add = new Button("Add");
        delete.setUserData(index);

        delete.setOnAction(event -> {
            if (rowcount.get() > 1) {
                grid.getChildren().removeIf(node -> GridPane.getRowIndex(node) == delete.getUserData());
                rowcount.decrementAndGet();
            }
        });

        add.setOnAction(event -> {
            grid.addRow(grid.getRowCount(), emptyRow(grid.getRowCount()));
            rowcount.incrementAndGet();
        });

        return new Node[]{property, condition, expression, delete, add};
    }

    private Node[] headerRow() {
        return new Label[]{new Label("Property"), new Label("Condition"), new Label("Expression")};
    }

    private void initializeButtons() {
        ButtonBar buttonBar = new ButtonBar();

        Button cancel = new Button(ButtonType.CANCEL.getText());
        ButtonBar.setButtonData(cancel, ButtonBar.ButtonData.CANCEL_CLOSE);

        Button apply = new Button("Check");
        ButtonBar.setButtonData(apply, ButtonBar.ButtonData.OK_DONE);

        Button check = new Button(ButtonType.APPLY.getText());
        ButtonBar.setButtonData(check, ButtonBar.ButtonData.APPLY);

        cancel.setCancelButton(true);

        check.setOnAction(actionEvent -> {
            checkCallback.apply(getFilters());
        });

        buttonBar.getButtons().setAll(apply, cancel, check);
        vBox.getChildren().add(buttonBar);
    }

    @Override
    public List<Filter> getFilters() {
        int firstRowStartIndex = 3;
        List<Filter> filters = new ArrayList<Filter>(rowcount.get());
        for (int i = 0; i < rowcount.get(); i++) {
            filters.add(consumeRow(firstRowStartIndex + i * 5));
        }
        return filters;
    }

    private Filter consumeRow(int startIndex) {
        ObservableList<Node> gridElements = grid.getChildren();
        return new Filter(
                resolveAttributeValue(gridElements.get(startIndex)),
                resolveCondition(gridElements.get(startIndex + 1)),
                ((TextField) (gridElements.get(startIndex + 2))).getText());
    }

    private String resolveCondition(Node node) {
        Object value = ((ChoiceBox) node).getValue();
        return value == null ? null : value.toString();
    }

    private String resolveAttributeValue(Node node) {
        return ((TextField) node).getText();
    }
}
