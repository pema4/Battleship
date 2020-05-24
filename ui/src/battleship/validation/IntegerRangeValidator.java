package battleship.validation;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Валидатор, проверяющий, лежит ли введённое число в нужном диапазоне
 */
public class IntegerRangeValidator implements Validator<String> {
    private final IntegerProperty min = new SimpleIntegerProperty(Integer.MIN_VALUE);
    private final IntegerProperty max = new SimpleIntegerProperty(Integer.MAX_VALUE);

    // все эти геттеры и сеттеры используются для задания значений параметрам в FXML

    public int getMin() {
        return min.get();
    }

    public void setMin(int min) {
        this.min.set(min);
    }

    public IntegerProperty minProperty() {
        return min;
    }

    public int getMax() {
        return max.get();
    }

    public void setMax(int max) {
        this.max.set(max);
    }

    public IntegerProperty maxProperty() {
        return max;
    }

    @Override
    public boolean isValid(String value) {
        try {
            var number = Integer.parseInt(value);
            return number >= min.get() && number < max.get();
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
