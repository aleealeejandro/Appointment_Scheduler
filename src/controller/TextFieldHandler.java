package controller;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyEvent;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 *
 * @author Alexander Padilla
 */
public class TextFieldHandler {

    /**
     * creates a text field handler with a character limit and regex filter
     *
     * @param i max number of characters
     * @param type type of text-field
     * @return event handler for text field
     */
    public static EventHandler<KeyEvent> maxLengthTextField(final Integer i, String type) {
        return new EventHandler<>() {
            @Override
            public void handle(KeyEvent e) {
                TextField txtField = (TextField) e.getSource();

                if(txtField.getText().length() >= i) {
                    e.consume();
                }

//                \p{Punct} includes !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~ but only accepting #&&'()*+,-./:@_ to avoid SQL Injection
                Pattern pattern;

                switch(type) {
                    case "password":
                    case "email":
                        pattern = Pattern.compile("[\\w[\\p{Punct}&&[#&-:@_]]]*");
                        break;
//                    case "regular":
//                        pattern = Pattern.compile("[\\w\\s[\\p{Punct}&&[#&-:@_]]]*");
//                        break;
                    default:
                        pattern = Pattern.compile("[\\w\\s[\\p{Punct}&&[#&-:@_]]]*");
                }

                UnaryOperator<TextFormatter.Change> filter = c -> {
                    if (pattern.matcher(c.getControlNewText()).matches()) {
                        return c;
                    } else {
                        return null;
                    }
                };
                TextFormatter<String> formatter = new TextFormatter<>(filter);
                txtField.setTextFormatter(formatter);
            }
        };
    }
}
