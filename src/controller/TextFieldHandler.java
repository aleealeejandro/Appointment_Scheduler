package controller;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 *
 * @author Alexander Padilla
 */
public class TextFieldHandler {

    /**
     * creates a text field handler with a character limit
     *
     * @param i max number of characters
     * @return event handler for text field
     */
    public static EventHandler<KeyEvent> maxLengthTextField(final Integer i) {
        return new EventHandler<>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txtField = (TextField) e.getSource();
                if(txtField.getText().length() >= i) {
                    e.consume();
                }
            }
        };
    }
}
