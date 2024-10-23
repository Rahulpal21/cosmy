package org.cosmy.controllers;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.cosmy.context.ConnectionsContainer;
import org.cosmy.spec.IController;
import org.cosmy.view.ItemsTab;

public class AccountsViewController implements IController {
    public static void launchItemsTab(MouseEvent mouseEvent) {
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
    }
}
