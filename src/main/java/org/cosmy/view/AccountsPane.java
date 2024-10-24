package org.cosmy.view;

import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.cosmy.context.ConnectionsContainer;
import org.cosmy.context.IObservableModelRegistry;
import org.cosmy.context.ObservableModelRegistryImpl;
import org.cosmy.controllers.AccountsViewController;
import org.cosmy.controllers.ItemsHandler;
import org.cosmy.model.CosmosAccount;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.CosmyException;
import org.cosmy.spec.IController;
import org.cosmy.spec.IVisualElement;
import org.cosmy.utils.FXMLConstants;
import org.cosmy.utils.FXMLUtils;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.Objects;

/// @author Rahul Pal
public class AccountsPane implements IVisualElement {

    private TreeView treeView;
    private final IController controller;
    private final ItemsHandler itemsHandler;
    private final IObservableModelRegistry modelRegistry;

    public AccountsPane() {
        controller = new AccountsViewController();
        modelRegistry = ObservableModelRegistryImpl.getInstance();
        itemsHandler = new ItemsHandler();
    }

    /**
     *
     */
    @Override
    public void initialize() {
        try {

            treeView = (TreeView) FXMLUtils.loadFXML(FXMLConstants.ACCOUNTS_PANE_FXML, controller);

            FontIcon icon = new FontIcon("mdal-account_circle");

            TreeItem<String> accountRoot = new TreeItem<>("Accounts", icon);
            accountRoot.setGraphic(icon);
            accountRoot.getGraphic().setVisible(true);

            ObservableModelRegistryImpl.getInstance().register(ObservableModelKey.ACCOUNTS, accountRoot.getChildren());
            treeView.setRoot(accountRoot);
            treeView.setCellFactory(getCustomCellFactory());
            restore();

        } catch (IOException e) {
            throw new CosmyException(e.getMessage(), e);
        }
    }

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
                cell.setOnKeyPressed(keyEvent -> {
                    //TODO investigate enter event on items cell
                    System.out.println(keyEvent);
                });

                return cell;
            }
        };

    }

    public void restore() {
        ObservableList<TreeItem<String>> accounts = (ObservableList<TreeItem<String>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        ConnectionsContainer.getInstance().iterateAccounts().forEachRemaining(cosmosAccount -> {
            TreeItem<String> item = generateEmptyCollapsedView(cosmosAccount);
            accounts.add(item);
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

    public static TreeItem<String> generateEmptyCollapsedView(CosmosAccount account) {
        Node icon = new ImageView(new Image(Objects.requireNonNull(AccountsPane.class.getResourceAsStream("/icons/telescope.png"))));
        TreeItem<String> item = new TreeItem<>(account.getName());

//        if (account.databaseCount() <= 0) {
        item.getChildren().add(new TreeItem<>());
//        }
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

    private static void expandAccountView(TreeItem<String> item, CosmosAccount account) {
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
        ObservableList<TreeItem<String>> accounts = (ObservableList<TreeItem<String>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        accounts.add(generateEmptyCollapsedView(account));
    }
}
