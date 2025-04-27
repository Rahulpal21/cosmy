package org.cosmy.controllers.itemtab;

import com.azure.cosmos.models.SqlQuerySpec;
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
    private Button reloadItemsButton;
    private Button clearFilterButton;
    private Button filterItemsButton;

    public QueryFilterController(ItemTabController parentController, CosmosContainer container, TextField filterQuery, Button reloadItemsButton, Button clearFilterButton, Button filterItemsButton) {
        this.parentController = parentController;
        this.container = container;
        this.reloadItemsButton = reloadItemsButton;
        this.clearFilterButton = clearFilterButton;
        this.filterItemsButton = filterItemsButton;
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

        filterItemsButton.setOnAction(event -> {
            showFilter();
        });

    }

    private void showFilter() {
        System.out.println("launch filter..");
    }

    public SqlQuerySpec getFilterQuery() {
        String readAllQuery = "SELECT c.id" + resolvePKeySelectClause() + " FROM c";
        if (filterSet.get()) {
            readAllQuery = readAllQuery.concat(" WHERE ").concat(filterString);
        }
        return new SqlQuerySpec(readAllQuery);
    }

    private @NotNull String resolvePKeySelectClause() {
        if (container.getPartitionKey() == null || "".equalsIgnoreCase(container.getPartitionKey()) || "id".equalsIgnoreCase(container.getPartitionKey())) {
            return "";
        }
        return ", c." + container.getPartitionKey();
    }

    private void clearFilter() {
        this.filterString = null;
        this.filterSet.set(false);
    }

}
