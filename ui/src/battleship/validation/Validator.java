package battleship.validation;

/**
 * Объект, используемый для валидации ввода в ValidatedTextField
 * @param <T> тип проверяемых объектов
 */
public interface Validator<T> {
    boolean isValid(T value);
}
