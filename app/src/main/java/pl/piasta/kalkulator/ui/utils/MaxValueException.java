package pl.piasta.kalkulator.ui.utils;

public class MaxValueException extends RuntimeException {

    public MaxValueException() {
        super("Osiągnięto maksymalną dozwoloną wartość");
    }

    public MaxValueException(String message) {
        super(message);
    }
}
