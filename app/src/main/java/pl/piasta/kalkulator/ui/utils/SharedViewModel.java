package pl.piasta.kalkulator.ui.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SharedViewModel extends ViewModel {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<String> mInputValue;

    private BigDecimal currentValue;
    private boolean isUpdatable;
    private boolean wasCleared;

    public SharedViewModel() {
        mInputValue = new MutableLiveData<>("");
        currentValue = BigDecimal.ZERO;
        isUpdatable = true;
    }

    public LiveData<String> getInputValue() {
        return mInputValue;
    }

    public void clear() {
        executor.execute(() -> {
            if (wasCleared) {
                currentValue = BigDecimal.ZERO;
            }
            mInputValue.postValue("");
            wasCleared = true;
            isUpdatable = true;
        });
    }

    public void clearAll() {
        executor.execute(() -> {
            mInputValue.postValue("");
            currentValue = BigDecimal.ZERO;
            wasCleared = false;
            isUpdatable = true;
        });
    }

    public void updateInputValue(CharSequence value) {
        executor.execute(() -> {
            String inputValue = Objects.requireNonNull(mInputValue.getValue());
            if ((value.equals("0") && inputValue.equals("0")) ||
                    (value.equals("0") && inputValue.equals("0.")) || !isUpdatable) {
                return;
            } else if (value.equals(".")) {
                if (inputValue.isEmpty()) {
                    mInputValue.postValue("0.");
                    return;
                } else if (inputValue.contains(value)) {
                    return;
                }
            }
            mInputValue.postValue(inputValue + value);
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
        });
    }
}