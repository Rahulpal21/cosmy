package org.cosmy.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Function;

import static org.cosmy.utils.IconConstants.*;

public class DefaultStyledDialogFactory implements DialogFactory {
    private static String ICON_ERROR = ALERT_ICON_TRIANGLE;
    private static String ICON_WARN = ALERT_ICON_TRIANGLE;
    private static String ICON_INFO = ALERT_ICON_ROUND;
    private static String ICON_CONFIRM = ALERT_ICON_QUESTION;
    private static String TITLE_ERROR = "Error";
    private static String TITLE_WARN = "Warning";
    private static String TITLE_INFO = "Information";
    private static String TITLE_CONFIRMATION = "Confirmation";

    DefaultStyledDialogFactory() {
    }

    @Override
    public Dialog createErrorDialog(String message) {
        return createNewDialog(Alert.AlertType.ERROR, ICON_ERROR, TITLE_ERROR, message, DefaultStyledDialogFactory::decorateOKButtons);
    }

    @Override
    public Dialog createWarnDialog(String message) {
        return createNewDialog(Alert.AlertType.WARNING, ICON_WARN, TITLE_WARN, message, DefaultStyledDialogFactory::decorateOKButtons);
    }

    @Override
    public Dialog createInfoDialog(String message) {
        return createNewDialog(Alert.AlertType.INFORMATION, ICON_INFO, TITLE_INFO, message, DefaultStyledDialogFactory::decorateOKButtons);
    }

    @Override
    public Dialog createConfirmDialog(String message) {
        return createNewDialog(Alert.AlertType.CONFIRMATION, ICON_CONFIRM, TITLE_CONFIRMATION, message, DefaultStyledDialogFactory::decorateConfirmButtons);
    }

    private Dialog createNewDialog(Alert.AlertType alertType, String icon, String title, String message, Function<DialogPopup, DialogPopup> applyActionButtons) {
        DialogPopup dialog = new DialogPopup();
        dialog.setTitle(title);
        dialog.setGraphic(new FontIcon(icon));
        dialog.setMessageContainer(message);
        return applyActionButtons.apply(dialog);
    }


    private static DialogPopup decorateConfirmButtons(DialogPopup dialog) {
        dialog.enableOKButton();
        dialog.enableCancelButton();
        return dialog;
    }

    private static DialogPopup decorateOKButtons(DialogPopup dialog) {
        dialog.enableOKButton();
        return dialog;
    }

}
