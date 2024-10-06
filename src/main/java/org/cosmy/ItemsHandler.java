package org.cosmy;

import org.cosmy.ui.tabs.ITab;
import org.cosmy.ui.tabs.ItemsTab;

public class ItemsHandler {
    public void handle(String container, String database, String account) {
        ITab tab = new ItemsTab(container, database, account);
        tab.initialize();
    }
}
