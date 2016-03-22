package utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * @author Wojciech Pachuta.
 */
public class DoubleField extends TextField {
    final private DoubleProperty value;

    public double getValue() {
        return value.getValue();
    }

    public void setValue(double newValue) {
        value.setValue(newValue);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public DoubleField() {
        super();
        value = new SimpleDoubleProperty(0);
        setText(0 + "");

        final DoubleField doubleField = this;

        value.addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                doubleField.setText("");
            } else {
                if (newValue.intValue() == 0 && (textProperty().get() == null || "".equals(textProperty().get()))) {
                } else {
                    doubleField.setText(newValue.toString());
                }
            }
        });

        this.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (!"0123456789.".contains(keyEvent.getCharacter())) {
                keyEvent.consume();
            }
        });

        this.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null || "".equals(newValue)) {
                value.setValue(0);
                return;
            }

            value.set(Double.parseDouble(textProperty().get()));
        });
    }
}