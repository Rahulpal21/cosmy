package org.cosmy.ui.tabs;

import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.PartitionKey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.scene.control.TextArea;
import org.cosmy.model.CosmosContainer;

import java.util.Map;

public class ItemsTabContentPane extends TextArea {
    private ObjectMapper jsonPrinter;

    public ItemsTabContentPane(String s) {
        super(s);
        this.jsonPrinter = new ObjectMapper();
        this.jsonPrinter.registerModule(new JavaTimeModule());
        this.jsonPrinter.enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
    }

    public void loadItem(String id, Object partitionKey, CosmosContainer container) {
        // TODO error handling
        CosmosAsyncContainer asyncContainer = container.getAsyncContainer();

        // TODO support mutli-attribute partition keys

        asyncContainer.readItem(id, new PartitionKey(partitionKey), Map.class).handle((response, synchronousSink1) -> {
            try {
                this.setText(jsonPrinter.writeValueAsString(response.getItem()));
                // TODO
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
                //TODO error handling
            }
        }).doOnError(throwable -> {
            System.out.println(throwable);
        }).subscribe();
    }
}
