package battleship.controls;

import battleship.validation.Validator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.ColorInput;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ValidatedTextField extends TextField {
    private final ColorInput blendInput = new ColorInput();
    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(false);
    private final ObjectProperty<Validator<String>> validator = new SimpleObjectProperty<>();

    public ValidatedTextField() {
        blendInput.widthProperty().bind(widthProperty());
        blendInput.heightProperty().bind(heightProperty());
        blendInput.setPaint(Color.RED);

        Blend blend = new Blend();
        blend.setTopInput(blendInput);
        blend.setOpacity(0);
        setEffect(blend);
        textProperty().addListener((v, o, n) -> {
            var isValid = validator.get() == null || validator.get().isValid(n);
            valid.set(isValid);
            blend.setOpacity(isValid || n.isEmpty() ? 0 : 0.3);
        });
    }

    public Validator<String> getValidator() {
        return validator.get();
    }

    public void setValidator(Validator<String> validator) {
        this.validator.set(validator);
    }

    public ObjectProperty<? extends Validator<String>> validatorProperty() {
        return validator;
    }

    public boolean isValid() {
        return valid.get();
    }

    public ReadOnlyBooleanWrapper validProperty() {
        return valid;
    }

    public Paint getErrorPaint() {
        return blendInput.getPaint();
    }

    public void setErrorPaint(Paint errorPaint) {
        blendInput.setPaint(errorPaint);
    }

    public ObjectProperty<Paint> errorPaintProperty() {
        return blendInput.paintProperty();
    }
}
