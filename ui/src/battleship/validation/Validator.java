package battleship.validation;

public interface Validator<T> {
    boolean isValid(T value);
}
