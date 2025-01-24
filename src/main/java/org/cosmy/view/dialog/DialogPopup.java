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
        messageContainer.setPrefWidth(500);
        super.getDialogPane().getButtonTypes().add(ButtonType.OK);
        getDialogPane().setContent(messageContainer);

    }

    void enableOKButton() {
//        okButton.setVisible(true);
    }

    void enableCancelButton() {
//        cancelButton.setVisible(true);
    }

    public void setMessageContainer(String message) {
        message = shortenAndExtractTooltip(message);
        messageContainer.setText(message);
    }

    private String shortenAndExtractTooltip(String message) {
        if (message.length() > 256) {
            tooltip = new Tooltip(message);
            tooltip.setWrapText(true);
            messageContainer.setTooltip(tooltip);
            return message.substring(0, 255).concat("...");
        } else {
            return message;
        }
    }
}
