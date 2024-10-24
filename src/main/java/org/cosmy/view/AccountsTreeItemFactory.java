package org.cosmy.view;

import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import org.kordamp.ikonli.javafx.FontIcon;

public class AccountsTreeItemFactory {

    private static AccountsTreeItemFactory instance;

    public static AccountsTreeItemFactory getInstance() {
        if (instance == null) {
            synchronized (AccountsTreeItemFactory.class) {
                if (instance == null) {
                    instance = new AccountsTreeItemFactory();
                }
            }
        }
        return instance;
    }

    public TreeItem<Label> newTreeItem(Label label, FontIcon icon) {
        TreeItem<Label> treeItem = new TreeItem<>(label, icon);
        treeItem.addEventHandler(EventType.ROOT, event -> {
            System.out.println(">>>>>>>  " + event);
        });
        label.setOnMouseClicked(mouseEvent -> {
            System.out.println("*******  " + mouseEvent);
            label.requestFocus();
        });
        return treeItem;
    }
}
