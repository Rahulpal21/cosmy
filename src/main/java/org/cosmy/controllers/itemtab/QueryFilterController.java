package org.cosmy.controllers.itemtab;

import com.azure.cosmos.models.SqlQuerySpec;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.cosmy.model.CosmosContainer;
import org.cosmy.spec.IController;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class QueryFilterController implements IController {
    private String filterString;
    private AtomicBoolean filterSet = new AtomicBoolean(false);

    private CosmosContainer container;
    private ItemTabController parentController;
    private TextField filterQuery;
    private Button reloadItemsButton;
    private Button clearFilterButton;

    public QueryFilterController(ItemTabController parentController, CosmosContainer container, TextField filterQuery, Button reloadItemsButton, Button clearFilterButton) {
        this.parentController = parentController;
        this.container = container;
        this.filterQuery = filterQuery;
        this.reloadItemsButton = reloadItemsButton;
        this.clearFilterButton = clearFilterButton;
    }

    @Override
    public void initialize() {
        reloadItemsButton.setOnAction(event -> {
            parentController.reloadItems();
        });

        clearFilterButton.setOnAction(event -> {
            clearFilter();
            parentController.loadItems();
        });

        filterQuery.setOnMouseClicked(mouseEvent -> {
            if (!filterSet.get()) {
                filterQuery.clear();
                filterQuery.setEditable(true);
            }
        });

        filterQuery.setOnAction(event -> {
            if (event.getEventType().equals(ActionEvent.ACTION)) {
                setFilterString();
                parentController.loadItems();
            }
        });
    }

    public SqlQuerySpec getFilterQuery() {
        String readAllQuery = "SELECT c.id" + resolvePKeySelectClause() + " FROM c";
        if (filterSet.get()) {
            readAllQuery = readAllQuery.concat(" WHERE ").concat(filterString);
        }
        return new SqlQuerySpec(readAllQuery);
    }

    private @NotNull String resolvePKeySelectClause() {
        if(container.getPartitionKey() == null || "".equalsIgnoreCase(container.getPartitionKey()) || "id".equalsIgnoreCase(container.getPartitionKey())){
            return "";
        }
        return ", c." + container.getPartitionKey();
    }

    private void clearFilter() {
        this.filterQuery.clear();
        this.filterQuery.setText("SELECT * FROM c WHERE");
        this.filterQuery.setEditable(false);
        this.filterString = null;
        this.filterSet.set(false);
    }

    private void setFilterString() {
        if (validateFilterString(this.filterQuery.getText())) {
            this.filterString = this.filterQuery.getText();
            filterSet.set(true);
        }
    }

    private boolean validateFilterString(String filterString) {
        if (filterString != null && !filterString.isEmpty()) {
            // TODD filter query validation rules
            return true;
        }
        return false;
    }

}
