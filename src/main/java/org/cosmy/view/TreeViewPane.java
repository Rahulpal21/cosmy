package org.cosmy.view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.cosmy.*;
import org.cosmy.controllers.TreeViewPaneController;
import org.cosmy.model.ObservableModelKey;
import org.cosmy.spec.CosmyException;
import org.cosmy.spec.IController;
import org.cosmy.spec.IVisualElement;
import org.cosmy.utils.FXMLConstants;
import org.cosmy.utils.FXMLUtils;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

/// @author Rahul Pal
public class TreeViewPane implements IVisualElement {

    private TreeView treeView;
    private final IController controller;
    private final ItemsHandler itemsHandler;
    private final IObservableModelRegistry modelRegistry;

    public TreeViewPane() {
        controller = new TreeViewPaneController();
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

            TreeItem<String> accountRoot = new TreeItem<>("Accounts");
            FontIcon icon = new FontIcon("mdomz-supervisor_account");

            accountRoot.setGraphic(icon);
            accountRoot.getGraphic().setVisible(true);

            modelRegistry.register(ObservableModelKey.ACCOUNTS, accountRoot.getChildren());
            customCellFactory(treeView, accountRoot);
            restore();
        } catch (IOException e) {
            throw new CosmyException(e.getMessage(), e);
        }
    }

    private void customCellFactory(TreeView<String> dbAccounts, TreeItem<String> accountRoot) {
        dbAccounts.setCellFactory(treeView -> {
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
            cell.setOnMouseClicked(mouseEvent -> {
                Node interactedNode = mouseEvent.getPickResult().getIntersectedNode();
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_CLICKED && mouseEvent.getClickCount() == 2 && interactedNode instanceof Text) {
                    Text node = (Text) interactedNode;
                    String nodeName = node.getText();
                    if (nodeName.equalsIgnoreCase("Items")) {
                        if (node.getParent() instanceof TreeCell) {
                            TreeItem<String> treeItem = ((TreeCell<String>) node.getParent()).getTreeItem();
                            TreeItem<String> container = treeItem.getParent();
                            TreeItem<String> database = container.getParent();
                            TreeItem<String> account = database.getParent();
                            new ItemsTab(ConnectionsContainer.getInstance().getConnection(account.getValue()).getDatabase(database.getValue()).getContainer(container.getValue())).initialize();
                        }
                    }
                }
            });
            cell.setOnKeyReleased(keyEvent -> {
                //TODO investigate enter event on items cell
                System.out.println(keyEvent);
            });
            cell.setOnKeyPressed(keyEvent -> {
                //TODO investigate enter event on items cell
                System.out.println(keyEvent);
            });

            return cell;
        });

        dbAccounts.setRoot(accountRoot);
    }

    public void restore() {
        ObservableList<TreeItem<String>> accounts = (ObservableList<TreeItem<String>>) ObservableModelRegistryImpl.getInstance().lookup(ObservableModelKey.ACCOUNTS);
        ConnectionsContainer.getInstance().iterateAccounts().forEachRemaining(cosmosAccount -> {
            TreeItem<String> item = AccountViewGenerators.generateEmptyCollapsedView(cosmosAccount);
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
}
