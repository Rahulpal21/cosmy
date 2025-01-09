package org.cosmy.controllers;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.model.CosmosAccount;
import org.cosmy.model.CosmosContainer;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.IController;
import org.cosmy.utils.MessageConstants;
import org.cosmy.view.*;

public class AccountsViewController implements IController {

    public static void launchItemsTab(MouseEvent mouseEvent) {
        Node interactedNode = mouseEvent.getPickResult().getIntersectedNode();
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED && mouseEvent.getClickCount() == 2 && interactedNode instanceof Text) {
            Text node = (Text) interactedNode;
            String nodeName = node.getText();
            if (nodeName.equalsIgnoreCase("Items")) {
                if (node.getParent() instanceof AccountsTreeNode) {
                    CosmosContainer container = (CosmosContainer) node.getParent().getUserData();
                    if (container != null) {
                        new ItemsTab(container).initialize();
                    }
                }
            }
        }
    }

    private static void handleExpandAction(CosmosAccount account, TreeItem<AccountsTreeNode> treeItem) {
        //start progress indicator
        if(!(treeItem.getValue() instanceof AccountsTreeNode)){
            return;
        }
        AccountsTreeNode treeItemNode = (AccountsTreeNode) treeItem.getValue();
        if(treeItemNode.getLevel()!=AccountsTreeLevels.ACCOUNT){
            return;
        }

        if(account.isAccountRefreshed()){
            return;
        }

        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setPrefSize(16, 16);
        TreeItem<AccountsTreeNode> indicatorItem = new TreeItem<>(new AccountsTreeNode("loading..", AccountsTreeLevels.LOADING_INDICATOR), indicator);
        treeItem.getChildren().clear();
        treeItem.getChildren().add(0, indicatorItem);

        //submit task in background for loading accounts
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                expandAccountView(treeItem, account);
                return null;
            }
        };
        new Thread(task, MessageConstants.ACCOUNT_LOAD_THREAD_NAME).start();
    }

    private static void expandAccountView(TreeItem<AccountsTreeNode> item, CosmosAccount account) {
        if (!account.isAccountRefreshed()) {
            try {
                account.refresh();
                account.iterateDatabases().forEachRemaining(cosmosDatabase -> {
                    item.getChildren().add(cosmosDatabase.generateView());
                });
            } catch (Throwable t) {
                System.out.println(t);
            }

            account.setAccountRefreshed(true);
            item.getChildren().removeFirst();
        }
    }

    public void acceptNewAccount(CosmosAccount account) {
        ObservableList<TreeItem<AccountsTreeNode>> accounts = (ObservableList<TreeItem<AccountsTreeNode>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        accounts.add(generateEmptyCollapsedView(account));
    }

    public static TreeItem<AccountsTreeNode> generateEmptyCollapsedView(CosmosAccount account) {
        TreeItem<AccountsTreeNode> item = AccountsTreeItemFactory.getInstance().newTreeItem(account.getName(), AccountsTreeLevels.ACCOUNT);
        item.getChildren().add(new TreeItem<>());

        EventType<TreeItem.TreeModificationEvent<AccountsTreeNode>> eventType = TreeItem.branchExpandedEvent();

        item.addEventHandler(eventType, event -> {
            handleExpandAction(account, event.getTreeItem());
        });
        return item;
    }

    @Override
    public void initialize() {

    }

    public static void launchQueryTab(ActionEvent event) {
        MenuItem eventSource = (MenuItem) event.getSource();
        CosmosContainer container = (CosmosContainer) eventSource.getUserData();
        new QueryTab(container).initialize();
        System.out.println("query tab launched");
    }
}
