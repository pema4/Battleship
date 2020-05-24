package battleship.validation;

public class NotBlankValidator implements Validator<String> {
    @Override
    public boolean isValid(String value) {
        return !value.isBlank();
    }
}
