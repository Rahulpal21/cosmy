package org.cosmy.view;

import javafx.scene.control.Label;

public class AccountsTreeNode extends Label {
    private int level;

    public AccountsTreeNode(String text, int level) {
        super(text);
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
