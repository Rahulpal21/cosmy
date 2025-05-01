package org.cosmy.controllers.itemtab;

public class FilterGridCondition {
    private final String key;
    private final String label;

    public FilterGridCondition(String key, String label) {
        this.key = key;
        this.label = label;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }
}
