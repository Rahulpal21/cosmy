package org.cosmy.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmy.controllers.itemtab.JsonPrinterFactory;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import static org.cosmy.utils.StyleConstants.*;

public class JsonTextArea extends CodeArea {
    private final ObjectMapper jsonPrinter = JsonPrinterFactory.getJsonPrinter();
    private IndentationHelper indentationHelper;

    public JsonTextArea() {
        super();
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        autosize();
        indentationHelper = new IndentationHelper();
    }

    public void setText(JsonNode jsonNode) {
        this.clear();
        appendJsonStartParenthesis();
        jsonNode.fields().forEachRemaining(nodeEntry -> {
            appendAttributeKey(nodeEntry.getKey());
            appendAttributeValue(nodeEntry.getValue());
            appendNewline();
        });
        appendJsonEndParenthesis();
    }

    public void setText(String text) throws JsonProcessingException {
        try {
            setText(jsonPrinter.readTree(text));
        } catch (JsonProcessingException e) {
            throw e;
        }
    }

    private void appendNewline() {
        this.append(NEWLINE, "");
    }

    private void appendJsonEndParenthesis() {
        this.append(END_PARENTHESIS, STYLE_KEY);
        indentationHelper.decrementIndent();
    }

    private void appendJsonStartParenthesis() {
        this.append(START_PARENTHESIS, STYLE_KEY);
        indentationHelper.incrementIndent();
    }

    private void appendAttributeValue(JsonNode value) {
        this.append(SINGLE_QUOTE + value.asText("") + SINGLE_QUOTE, STYLE_VALUE);
    }

    private void appendAttributeKey(String key) {
        this.append(indentationHelper.indent() + SINGLE_QUOTE + key + SINGLE_QUOTE + COLON, STYLE_KEY);
    }

    public String getText() {
        return super.getText();
    }
}
