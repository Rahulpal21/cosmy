package org.cosmy.model;

import org.cosmy.state.FilePersistedStateManager;

import java.io.IOException;
import java.io.Serializable;

public class Preferences implements Serializable {
    private static final long serialVersionUID = 123456L;
    private static Preferences instance;

    public static synchronized Preferences getInstance() {
        if (instance == null) {
            synchronized (Preferences.class) {
                if (instance == null) {
                    try {
                        instance = (Preferences) FilePersistedStateManager.getInstance().load(Preferences.class);
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println(e);
                        instance = new Preferences();
                    }
                }
            }
        }
        return instance;
    }

    private int pageLength;

    public int getPageLength() {
        return pageLength;
    }

    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }
}
