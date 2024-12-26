package org.cosmy.controllers.itemtab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonPrinterFactory {
    public static ObjectMapper getJsonPrinter() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
    }
}
