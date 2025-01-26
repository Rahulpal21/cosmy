package org.cosmy.utils;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.cosmy.view.dialog.DialogFactory;

public class DialogUtils {
    private static DialogFactory factory = DialogFactory.instance();

    public static void showErrorDialog(String message) {
        Dialog<String> dialog = factory.createErrorDialog(message);
        dialog.showAndWait();
    }

    public static void showSuccessDialog(String message) {
        Dialog<String> dialog = factory.createInfoDialog(message);
        dialog.showAndWait();
    }

    public static boolean showConfirmDialog(String message) {
        Dialog<ButtonType> dialog = factory.createConfirmDialog(message);
        return !dialog.showAndWait().get().getButtonData().isCancelButton();
    }
}
