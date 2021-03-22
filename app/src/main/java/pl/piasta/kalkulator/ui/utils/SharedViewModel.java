package pl.piasta.kalkulator.ui.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedViewModel extends ViewModel {

    private static final String ERROR_MESSAGE = "Error";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final SingleLiveEvent<String> mToastMessage = new SingleLiveEvent<>();
    private final MutableLiveData<String> mInputValue = new MutableLiveData<>("");

    private BigDecimal currentValue = BigDecimal.ZERO;
    private MathOperation latestOperation = MathOperation.ADD;
    private boolean isUpdatable = true;
    private boolean wasCleared;
    private boolean resultShown = true;

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
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if (inputValue.equals(ERROR_MESSAGE) || latestOperation.equals(MathOperation.SHOW_RESULT)) {
                resetAllCalculations();
                inputValue = "";
            } else if (resultShown) {
                resetCurrentCalculations();
                wasCleared = false;
                inputValue = "";
            } else if ((value.equals("0") && inputValue.equals("0")) ||
                    (value.equals("0") && inputValue.equals("0.")) || !isUpdatable
            ) {
                return;
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
            mInputValue.postValue(inputValue + value);
            resultShown = false;
        });
    }

    public void readMathOperation(String value, MathOperation operation) {
        executor.execute(() -> {
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if (!inputValue.isEmpty() && !resultShown) {
                try {
                    if (!latestOperation.equals(MathOperation.SHOW_RESULT)) {
                        calculate(value, latestOperation);
                    }
                    mInputValue.postValue(currentValue.toPlainString());
                } catch (DivisionByZeroException ex) {
                    mToastMessage.postValue(ex.getMessage());
                    return;
                } catch (Exception ex) {
                    mInputValue.postValue(ERROR_MESSAGE);
                }
            }
            latestOperation = operation;
            resultShown = true;
            isUpdatable = true;
        });
    }

    public void switchSign() {
        executor.execute(() -> {
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if (inputValue.isEmpty() || inputValue.equals("0") || inputValue.equals("0.")) {
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
                currentValue = new BigDecimal(inputValue);
            }
        });
    }

    private void calculate(String value, MathOperation operation) {
        BigDecimal newValue = new BigDecimal(value);
        switch (operation) {
            case ADD:
                currentValue = currentValue.add(newValue).stripTrailingZeros();
                break;
            case SUBTRACT:
                currentValue = currentValue.subtract(newValue).stripTrailingZeros();
                break;
            case MULTIPLY:
                currentValue = currentValue.multiply(newValue).stripTrailingZeros();
                break;
            case DIVIDE:
                if (newValue.equals(BigDecimal.ZERO)) {
                    throw new DivisionByZeroException();
                }
                currentValue = currentValue.divide(newValue, 9, RoundingMode.DOWN);
                int truncate = String.valueOf(currentValue.intValue()).length() - 1;
                currentValue = currentValue.setScale(Math.max(9 - truncate, 1), RoundingMode.DOWN).stripTrailingZeros();
                break;
        }
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
        currentValue = BigDecimal.ZERO;
        latestOperation = MathOperation.ADD;
        wasCleared = false;
        isUpdatable = true;
        resultShown = true;
        mInputValue.postValue("");
    }
}