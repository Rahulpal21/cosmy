package org.cosmy.ui.predicates;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;

public class MouseDoubleClickEvent {
    public static boolean evaluate(Event event) {
        if (event instanceof MouseEvent && event.getEventType() == MouseEvent.MOUSE_CLICKED && ((MouseEvent) event).getClickCount() == 2) {
            return true;
        }
        return false;
    }
}
