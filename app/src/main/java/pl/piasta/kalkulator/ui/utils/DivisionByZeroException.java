package pl.piasta.kalkulator.ui.utils;

public class DivisionByZeroException extends RuntimeException {

    public DivisionByZeroException() {
        super("Nie można dzielić przez zero");
    }

    public DivisionByZeroException(String message) {
        super(message);
    }
}
