package org.cosmy.view;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogPopup extends Dialog<String> {
    public DialogPopup(String message) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setResizable(true);
        dialog.setHeight(180);
        dialog.setWidth(500);
        dialog.setTitle("Error");
        ButtonType okButton = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText(message);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        dialog.showAndWait();
    }
}
