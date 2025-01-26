package org.cosmy.view.dialog;

import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.StageStyle;
import org.cosmy.App;

public class DialogPopup extends Dialog {
    private static String OK = "Ok";
    private static String CANCEL = "Cancel";
    private static Parent parent;
    private final Label messageContainer;
    private Tooltip tooltip;

    DialogPopup() {
        super();
        super.initOwner(App.mainStage);
        super.initStyle(StageStyle.UTILITY);
        messageContainer = new Label();
        messageContainer.setWrapText(true);
        messageContainer.setPrefWidth(350);
        getDialogPane().setContent(messageContainer);
    }

    void enableOKButton() {
        super.getDialogPane().getButtonTypes().add(ButtonType.OK);
    }

    void enableCancelButton() {
        super.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
    }

    public void setMessageContainer(String message) {
        message = shortenAndExtractTooltip(message);
        messageContainer.setText(message);
    }

    private String shortenAndExtractTooltip(String message) {
        if (message.length() > 256) {
            tooltip = new Tooltip(message);
            tooltip.setWrapText(true);
            tooltip.setPrefWidth(messageContainer.getPrefWidth());
            messageContainer.setTooltip(tooltip);
            return message.substring(0, 255).concat("...");
        } else {
            return message;
        }
    }
}
