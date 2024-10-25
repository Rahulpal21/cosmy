package org.cosmy.controllers;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import org.cosmy.model.CosmosContainer;
import org.cosmy.spec.IController;
import org.cosmy.view.AccountsTreeNode;
import org.cosmy.view.ItemsTab;

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
}
