package pl.piasta.kalkulator.ui.utils;

import java.util.Arrays;

public enum MathOperation {

    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    EXPONENT("x^y"),
    SHOW_RESULT("=");

    private final String value;

    MathOperation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MathOperation findByValue(String value) {
        return Arrays.stream(values())
                .filter(e -> e.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
