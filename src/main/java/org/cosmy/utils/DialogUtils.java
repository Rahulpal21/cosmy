package org.cosmy.utils;

import javafx.scene.control.Dialog;
import org.cosmy.view.dialog.DialogFactory;

public class DialogUtils {
    private static DialogFactory factory = DialogFactory.instance();

    public static void showErrorDialog(String message) {
        Dialog<String> dialog = factory.createErrorDialog(message);
        dialog.showAndWait();
    }
}
