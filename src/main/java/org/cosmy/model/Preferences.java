package org.cosmy.model;

import java.io.Serializable;

public class Preferences implements Serializable {
    private static final long serialVersionUID = 123456L;

    private int pageLength;

    public int getPageLength() {
        return pageLength;
    }

    public void setPageLength(int pageLength) {
        this.pageLength = pageLength;
    }
}
