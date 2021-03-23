package pl.piasta.kalkulator.ui.utils;

import java.util.Arrays;

public enum MathFormat {

    PERCENTAGE("%"),
    SQUARE_ROOT("x^2"),
    SQRT("sqrt"),
    SIN("sin"),
    COS("cos"),
    TAN("tan"),
    LN("ln"),
    LOG("log");

    private final String value;

    MathFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static MathFormat findByValue(String value) {
        return Arrays.stream(values())
                .filter(e -> e.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
