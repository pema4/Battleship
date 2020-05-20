package battleship.validation;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class IPValidator implements Validator<String> {
    private static final Predicate<String> IPV4_ADDRESS =
            Pattern.compile("localhost|\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}").asPredicate();

    @Override
    public boolean isValid(String value) {
        return IPV4_ADDRESS.test(value);
    }
}
