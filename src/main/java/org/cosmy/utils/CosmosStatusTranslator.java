package org.cosmy.utils;

public interface CosmosStatusTranslator {
    int SUCCESS_NO_CONTENT = 204;
    int SUCCESS = 200;
    int SUCCESS_CREATE = 201;
    int BAD_REQUEST = 400;

    static String translate(int code, CosmyCosmosOperation operation) {
        switch (operation) {
            case DELETE -> {
                return forDeleteContext(code);
            }
            case SAVE -> {
                return forSaveContext(code);
            }
            default -> {
                return "Unknown Status";
            }
        }
    }

    static String forSaveContext(int code) {
        switch (code) {
            case SUCCESS -> {
                return "Item saved/updated";
            }
        }
        return "Unknown Status";
    }

    static String forDeleteContext(int code) {
        switch (code) {
            case SUCCESS_NO_CONTENT -> {
                return "Item deleted";
            }
        }
        return "Unknown Status";
    }
}
