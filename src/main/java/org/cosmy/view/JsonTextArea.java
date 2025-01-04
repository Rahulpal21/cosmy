package org.cosmy.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cosmy.controllers.itemtab.JsonPrinterFactory;
import org.cosmy.utils.StyleConstants;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import static org.cosmy.utils.StyleConstants.*;

public class JsonTextArea extends CodeArea {
    private final ObjectMapper jsonPrinter = JsonPrinterFactory.getJsonPrinter();

    public JsonTextArea() {
        super();
        setParagraphGraphicFactory(LineNumberFactory.get(this));
        autosize();
    }

    public void setText(JsonNode text) {
        //TODO styled implementation
    }

    public void setText(String text) throws JsonProcessingException {
        //TODO styled implementation
        this.clear();
        try {
            JsonNode jsonNode = jsonPrinter.readTree(text);
            appendJsonStartParenthesis();
            jsonNode.fields().forEachRemaining(nodeEntry -> {
                appendAttributeKey(nodeEntry.getKey());
                appendAttributeValue(nodeEntry.getValue());
                appendNewline();
            });
            appendJsonEndParenthesis();
        } catch (JsonProcessingException e) {
            throw e;
        }
    }

    private void appendNewline() {
        this.append(NEWLINE, "");
    }

    private void appendJsonEndParenthesis() {
        this.append(END_PARENTHESIS, STYLE_KEY);
    }

    private void appendJsonStartParenthesis() {
        this.append(START_PARENTHESIS, STYLE_KEY);
    }

    private void appendAttributeValue(JsonNode value) {
        this.append(SINGLE_QUOTE + value.asText("") + SINGLE_QUOTE, STYLE_VALUE);
    }

    private void appendAttributeKey(String key) {
        this.append(SINGLE_QUOTE + key + SINGLE_QUOTE + COLON, STYLE_KEY);
    }

    public String getText() {
        //TODO styled implementation
        return "";
    }
}
