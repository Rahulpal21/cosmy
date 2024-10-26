package org.cosmy.view;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
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
import org.cosmy.utils.MessageConstants;

import java.io.IOException;

/// @author Rahul Pal
public class AccountsPane implements IVisualElement {

    private TreeView<AccountsTreeNode> treeView;
    private final AccountsViewController controller;

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
            /*treeView.addEventHandler(EventType.ROOT, event -> {
                System.out.println(">>>>>>>>>>   "+event.getEventType().getName());
            });*/
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
            accounts.add(AccountsViewController.generateEmptyCollapsedView(cosmosAccount));
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


    public void acceptNewAccount(CosmosAccount account) {
        controller.acceptNewAccount(account);
    }
}
