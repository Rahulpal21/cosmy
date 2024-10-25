package org.cosmy.view;

import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import org.cosmy.context.ConnectionsContainer;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.controllers.AccountsViewController;
import org.cosmy.model.CosmosAccount;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.CosmyException;
import org.cosmy.spec.IController;
import org.cosmy.spec.IVisualElement;
import org.cosmy.utils.FXMLConstants;
import org.cosmy.utils.FXMLUtils;

import java.io.IOException;

/// @author Rahul Pal
public class AccountsPane implements IVisualElement {

    private TreeView<AccountsTreeNode> treeView;
    private final IController controller;

    public AccountsPane() {
        controller = new AccountsViewController();
    }

    /**
     *
     */
    @Override
    public void initialize() {
        try {
            treeView = (TreeView<AccountsTreeNode>) FXMLUtils.loadFXML(FXMLConstants.ACCOUNTS_PANE_FXML, controller);
            TreeItem<AccountsTreeNode> accountRoot = AccountsTreeItemFactory.getInstance().newTreeItem("Accounts", AccountsTreeLevels.ROOT);
            ObservableModelRegistryImpl.getInstance().register(ObservableModelKey.ACCOUNTS, accountRoot.getChildren());
            treeView.setRoot(accountRoot);
            restore();
        } catch (IOException e) {
            throw new CosmyException(e.getMessage(), e);
        }
    }

    //this method is left for future reference
    private Callback<TreeView<String>, TreeCell<String>> getCustomCellFactory() {
        return new Callback<>() {

            @Override
            public TreeCell<String> call(TreeView<String> stringTreeView) {
                TreeCell<String> cell = new TreeCell<>() {
                    @Override
                    protected void updateItem(String s, boolean b) {
                        super.updateItem(s, b);
                        if (b) {
                            setText(null);
                        } else {
                            setText(s);
                        }
                    }
                };

                cell.setOnMouseClicked(AccountsViewController::launchItemsTab);

                cell.setOnKeyReleased(keyEvent -> {
                    //TODO investigate enter event on items cell
                    System.out.println(keyEvent);
                });
                return cell;
            }
        };

    }

    public void restore() {
        ObservableList<TreeItem<AccountsTreeNode>> accounts = (ObservableList<TreeItem<AccountsTreeNode>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        ConnectionsContainer.getInstance().iterateAccounts().forEachRemaining(cosmosAccount -> {
            accounts.add(generateEmptyCollapsedView(cosmosAccount));
        });
    }

    /**
     *
     */
    @Override
    public void reset() {

    }

    /**
     *
     */
    @Override
    public void registerActionListener() {

    }

    public TreeView getTreeView() {
        return treeView;
    }

    public static TreeItem<AccountsTreeNode> generateEmptyCollapsedView(CosmosAccount account) {

        TreeItem<AccountsTreeNode> item = AccountsTreeItemFactory.getInstance().newTreeItem(account.getName(), AccountsTreeLevels.ACCOUNT);

        item.getChildren().add(new TreeItem<>());

        item.addEventHandler(EventType.ROOT, event -> {
            switch (event.getEventType().getName()) {
                case "BranchExpandedEvent":
                    expandAccountView(item, account);
                    break;
                default:
                    System.out.println("Unhandled event: " + event.getEventType().getName());
            }
        });
        return item;
    }

    private static void expandAccountView(TreeItem<AccountsTreeNode> item, CosmosAccount account) {
        if (!account.isAccountRefreshed()) {
            item.getChildren().removeFirst();
            account.refresh();
            account.iterateDatabases().forEachRemaining(cosmosDatabase -> {
                item.getChildren().add(cosmosDatabase.generateView());
            });
            account.setAccountRefreshed(true);
        }
    }

    public void acceptNewAccount(CosmosAccount account) {
        ObservableList<TreeItem<AccountsTreeNode>> accounts = (ObservableList<TreeItem<AccountsTreeNode>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        accounts.add(generateEmptyCollapsedView(account));
    }
}
