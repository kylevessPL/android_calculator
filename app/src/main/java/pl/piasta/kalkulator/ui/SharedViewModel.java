package pl.piasta.kalkulator.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import pl.piasta.kalkulator.ui.utils.DivisionByZeroException;
import pl.piasta.kalkulator.ui.utils.MathFormat;
import pl.piasta.kalkulator.ui.utils.MathOperation;
import pl.piasta.kalkulator.ui.utils.MaxValueException;
import pl.piasta.kalkulator.ui.utils.SingleLiveEvent;

public class SharedViewModel extends ViewModel {

    private static final String ERROR_MESSAGE = "NaN";

    private final DecimalFormat decimalFormat;
    private final ExecutorService executor;

    private final SingleLiveEvent<String> mToastMessage;
    private final MutableLiveData<String> mInputValue;

    private double currentValue;
    private MathOperation latestOperation;
    private boolean isUpdatable;
    private boolean wasCleared;
    private boolean resultShown;

    public SharedViewModel() {
        decimalFormat = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));
        decimalFormat.setMaximumFractionDigits(10);
        executor = Executors.newSingleThreadExecutor();
        mToastMessage = new SingleLiveEvent<>();
        mInputValue = new MutableLiveData<>("");
        currentValue = 0;
        latestOperation = MathOperation.ADDITION;
        isUpdatable = true;
        resultShown = true;
    }

    public LiveData<String> getToastMessage() {
        return mToastMessage;
    }

    public LiveData<String> getInputValue() {
        return mInputValue;
    }

    public void clear() {
        executor.execute(this::resetCurrentCalculations);
    }

    public void clearAll() {
        executor.execute(this::resetAllCalculations);
    }

    public void updateInputValue(String value) {
        executor.execute(() -> {
            if (!isUpdatable) {
                return;
            }
            wasCleared = false;
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if ((inputValue.equals(ERROR_MESSAGE) || latestOperation.equals(MathOperation.SHOW_RESULT))) {
                resetAllCalculations();
                inputValue = "";
            } else if (resultShown || inputValue.equals("0")) {
                resetCurrentCalculations();
                wasCleared = false;
                inputValue = "";
            }
            if (value.equals(".")) {
                if (inputValue.isEmpty()) {
                    mInputValue.postValue("0.");
                    resultShown = false;
                    return;
                } else if (inputValue.contains(value)) {
                    return;
                }
            }
            inputValue += value;
            try {
                validateInput(inputValue);
            } catch (MaxValueException ex) {
                mToastMessage.postValue(ex.getMessage());
                return;
            }
            mInputValue.postValue(inputValue);
            resultShown = false;
        });
    }

    public void readMathOperation(String value, MathOperation operation) {
        executor.execute(() -> {
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if (!inputValue.isEmpty()) {
                if (!resultShown) {
                    try {
                        if (!latestOperation.equals(MathOperation.SHOW_RESULT)) {
                            calculate(value, latestOperation);
                        }
                        mInputValue.postValue(decimalFormat.format(currentValue));
                    } catch (DivisionByZeroException ex) {
                        mToastMessage.postValue(ex.getMessage());
                        return;
                    } catch (Exception ex) {
                        mInputValue.postValue(ERROR_MESSAGE);

                    }
                }
                latestOperation = operation;
            }
            resultShown = true;
            isUpdatable = true;
        });
    }

    public void switchSign() {
        executor.execute(() -> {
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if (inputValue.isEmpty() || inputValue.equals(ERROR_MESSAGE) ||
                    inputValue.equals("0") || inputValue.equals("0.")) {
                return;
            } else if (inputValue.lastIndexOf(".") == inputValue.length() - 1) {
                inputValue = inputValue.substring(0, inputValue.length() - 1);
            }
            if (inputValue.charAt(0) == '-') {
                inputValue = inputValue.substring(1);
            } else {
                inputValue = "-" + inputValue;
            }
            mInputValue.postValue(inputValue);
            isUpdatable = false;
            if (resultShown) {
                currentValue = Double.parseDouble(inputValue);
            }
        });
    }

    public void convertToFormat(MathFormat format) {
        executor.execute(() -> {
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if (inputValue.isEmpty()) {
                return;
            } else if (inputValue.lastIndexOf(".") == inputValue.length() - 1) {
                inputValue = inputValue.substring(0, inputValue.length() - 1);
            }
            double value = getValueInFormat(inputValue, format);
            try {
                validateResult(value);
                mInputValue.postValue(decimalFormat.format(value));
                if (resultShown) {
                    currentValue = value;
                }
            } catch (ArithmeticException ex) {
                mInputValue.postValue(ERROR_MESSAGE);
            }
            isUpdatable = false;
        });
    }

    private void calculate(String value, MathOperation operation) {
        double newValue = Double.parseDouble(value);
        switch (operation) {
            case ADDITION:
                currentValue += newValue;
                break;
            case SUBTRACTION:
                currentValue -= newValue;
                break;
            case MULTIPLICATION:
                currentValue *= newValue;
                break;
            case DIVISION:
                if (newValue == 0) {
                    throw new DivisionByZeroException();
                }
                currentValue /= newValue;
                break;
            case EXPONENT:
                currentValue = Math.pow(currentValue, newValue);
                break;
        }
        validateResult(currentValue);
    }

    private double getValueInFormat(String inputValue, MathFormat format) {
        double value = Double.parseDouble(inputValue);
        switch (format) {
            case PERCENTAGE:
                value /= 100;
                break;
            case SQUARE_ROOT:
                value = Math.pow(value, 2);
                break;
            case SQRT:
                value = Math.sqrt(value);
                break;
            case LN:
                value = Math.log(value);
                break;
            case LOG:
                value = Math.log10(value);
                break;
            case SIN:
                value = Math.sin(value);
                break;
            case COS:
                value = Math.cos(value);
                break;
            case TAN:
                value = Math.tan(value);
                break;
        }
        return value;
    }

    private void resetCurrentCalculations() {
        if (wasCleared) {
            resetAllCalculations();
            return;
        }
        wasCleared = true;
        isUpdatable = true;
        mInputValue.postValue("");
    }

    private void resetAllCalculations() {
        currentValue = 0;
        latestOperation = MathOperation.ADDITION;
        wasCleared = false;
        isUpdatable = true;
        resultShown = true;
        mInputValue.postValue("");
    }

    private void validateResult(double result) {
        if (Double.isInfinite(result)) {
            throw new ArithmeticException("Double overflow resulting in INFINITY");
        } else if (Double.isNaN(result)) {
            throw new ArithmeticException("Double overflow resulting in NaN");
        }
    }

    private void validateInput(String input) {
        if (input.replaceAll("[.-]", "").length() > 16) {
            throw new MaxValueException();
        }
    }
}