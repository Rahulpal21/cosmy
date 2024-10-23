package org.cosmy.context;

import org.cosmy.view.AccountsPane;

public class AppContext {
    private static AppContext instance;
    private AccountsPane accountsPane;
    
    public static AppContext getInstance() {
        if (instance == null) {
            synchronized (ConnectionsContainer.class) {
                if (instance == null) {
                    instance = new AppContext();
                }
            }
        }
        return instance;
    }
    
    public AccountsPane getAccountsPane() {
        return accountsPane;
    }

    public void setAccountsPane(AccountsPane accountsPane) {
        this.accountsPane = accountsPane;
    }
}
