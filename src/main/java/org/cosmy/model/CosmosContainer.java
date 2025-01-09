package org.cosmy.model;

import com.azure.cosmos.CosmosAsyncContainer;
import javafx.event.EventTarget;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.cosmy.controllers.AccountsViewController;
import org.cosmy.ui.ContainerDetails;
import org.cosmy.view.AccountsTreeItemFactory;
import org.cosmy.view.AccountsTreeLevels;
import org.cosmy.view.AccountsTreeNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CosmosContainer implements Serializable {
    private static final long serialVersionUID = 123456L;
    private String name;
    private transient com.azure.cosmos.CosmosContainer container;
    private transient CosmosAsyncContainer asyncContainer;
    private transient AtomicBoolean initialized = new AtomicBoolean(false);
    private transient CosmosDatabase parent;
    private transient ContainerDetails containerDetails;

    public CosmosContainer(String name, CosmosDatabase parent) {
        this.name = name;
        this.parent = parent;
    }

    public CosmosDatabase getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public com.azure.cosmos.CosmosContainer getContainer() {
        return container;
    }

    public CosmosAsyncContainer getAsyncContainer() {
        return asyncContainer;
    }

    public ContainerDetails getContainerDetails() {
        return containerDetails;
    }

    public void refresh() {
        container = this.parent.getDatabase().getContainer(this.getName());
        asyncContainer = this.parent.getAsyncDatabase().getContainer(this.getName());
        this.containerDetails = new ContainerDetails(container);
        initialized.set(true);
    }

    public TreeItem<AccountsTreeNode> generateView() {
        TreeItem<AccountsTreeNode> item = AccountsTreeItemFactory.getInstance().newTreeItem(this.name, AccountsTreeLevels.COLLECTION);
        generateContextMenu(item);
        item.getChildren().addAll(generateContainerOptions());
        return item;
    }

    private void generateContextMenu(TreeItem<AccountsTreeNode> item) {
        ContextMenu itemsContextMenu = new ContextMenu();
        MenuItem queryTabMenuOption = new MenuItem("Query Tab");
        queryTabMenuOption.setUserData(this);
        queryTabMenuOption.setOnAction(AccountsViewController::launchQueryTab);
        itemsContextMenu.getItems().add(queryTabMenuOption);
        item.getValue().setContextMenu(itemsContextMenu);
    }

    private List<TreeItem<AccountsTreeNode>> generateContainerOptions() {
        List<TreeItem<AccountsTreeNode>> options = new ArrayList<>();
        TreeItem<AccountsTreeNode> itemsExplorerOption = AccountsTreeItemFactory.getInstance().newTreeItem("Items", AccountsTreeLevels.ACTION);
        itemsExplorerOption.getValue().setUserData(this);
        decorateHandlers(itemsExplorerOption);
        options.add(itemsExplorerOption);
        return options;
    }

    private void decorateHandlers(TreeItem<AccountsTreeNode> itemsExplorerOption) {
        itemsExplorerOption.getValue().setOnMouseClicked(AccountsViewController::launchItemsTab);
    }

    private void loadDocumentsTab(EventTarget target) {
        System.out.println(target.getClass());
    }

    public String getPartitionKey() {
        String pKeyPath = getContainerDetails().getPartitionKeyPaths().getFirst();
        return pKeyPath.replace("/", "");
    }
}

