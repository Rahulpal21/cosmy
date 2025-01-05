package org.cosmy.view;
//TODO should be thread safe

public class IndentationHelper {
    private Integer indentCount;
    private String indentation = "";

    public IndentationHelper() {
        this.indentCount = 0;
    }

    public void incrementIndent() {
        indentCount++;
        indentation = recomputeIndentation();
    }

    public void decrementIndent() {
        indentCount--;
        indentation = recomputeIndentation();
    }

    private String recomputeIndentation() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Integer i = 0; i < indentCount; i++) {
            stringBuilder.append("\t");
        }
        return stringBuilder.toString();
    }

    public String indent() {
        return indentation;
    }
}
