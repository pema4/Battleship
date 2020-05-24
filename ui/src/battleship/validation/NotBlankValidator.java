package battleship.validation;

/**
 * Валидатор, проверяющий, содержит ли ввод что-то, кроме пробелов
 */
public class NotBlankValidator implements Validator<String> {
    @Override
    public boolean isValid(String value) {
        return !value.isBlank();
    }
}
