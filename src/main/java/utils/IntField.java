package utils;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * @author Wojciech Pachuta.
 */
public class IntField extends TextField {
    final private IntegerProperty value;

    public int  getValue()                 { return value.getValue(); }
    public void setValue(int newValue)     { value.setValue(newValue); }
    public IntegerProperty valueProperty() { return value; }

    public IntField(){
        super();
        value = new SimpleIntegerProperty(0);
        setText(0 + "");

        final IntField intField = this;

        value.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                intField.setText("");
            } else {
                if (newValue.intValue() == 0 && (textProperty().get() == null || "".equals(textProperty().get()))) {
                } else {
                    intField.setText(newValue.toString());
                }
            }
        });

        this.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });

        this.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || "".equals(newValue)) {
                value.setValue(0);
                return;
            }

            value.set(Integer.parseInt(textProperty().get()));
        });
    }
}

