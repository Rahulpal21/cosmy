package org.cosmy.view;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import org.cosmy.utils.IconConstants;
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

    public TreeItem<AccountsTreeNode> newTreeItem(String text, int level) {
        AccountsTreeNode node = new AccountsTreeNode(text);
        TreeItem<AccountsTreeNode> treeItem = new TreeItem<>(node, resolveIcon(level));
        return treeItem;
    }

    private Node resolveIcon(int level) {
        switch (level) {
            case AccountsTreeLevels.ROOT:
                return new FontIcon(IconConstants.ACCOUNTS_ROOT_ICON);
            case AccountsTreeLevels.ACCOUNT:
                return new FontIcon(IconConstants.ACCOUNT_ELEMENT_ICON);
            case AccountsTreeLevels.DATABASE:
                return new FontIcon(IconConstants.DATABASE_ELEMENT_ICON);
            case AccountsTreeLevels.COLLECTION:
                return new FontIcon(IconConstants.COLLECTION_ELEMENT_ICON);
            case AccountsTreeLevels.ACTION:
                return new FontIcon(IconConstants.ITEMS_OPTION_ICON);
        }
        return null;
    }

}
