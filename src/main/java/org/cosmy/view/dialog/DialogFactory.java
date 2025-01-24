package org.cosmy.view.dialog;

import javafx.scene.control.Dialog;

public interface DialogFactory {
    static DialogFactory instance() {
        return new DefaultStyledDialogFactory();
    }

    Dialog createErrorDialog(String message);
    Dialog createWarnDialog(String message);
    Dialog createInfoDialog(String message);
    Dialog createConfirmDialog(String message);

}
