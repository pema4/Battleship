package battleship.validation;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegexValidator implements Validator<String> {
    private final StringProperty regex = new SimpleStringProperty();

    public String getRegex() {
        return regex.get();
    }

    public void setRegex(String regex) {
        this.regex.set(regex);
    }

    public StringProperty regexProperty() {
        return regex;
    }

    @Override
    public boolean isValid(String value) {
        return value.isEmpty() || regex.get() == null || value.matches(regex.get());
    }
}
