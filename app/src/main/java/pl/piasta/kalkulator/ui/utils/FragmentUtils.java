package pl.piasta.kalkulator.ui.utils;

import android.view.View;
import android.widget.Button;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class FragmentUtils {

    public static List<View> getNumberButtons(List<View> touchables) {
        return touchables
                .stream()
                .filter(e -> e instanceof Button &&
                        (((Button) e).getText().equals(".") ||
                                IntStream.rangeClosed(0, 9)
                                        .mapToObj(Objects::toString)
                                        .collect(Collectors.toList())
                                        .contains(((Button) e).getText())))
                .collect(Collectors.toList());
    }

    public static List<View> getMathOperationButtons(List<View> touchables) {
        return touchables
                .stream()
                .filter(e -> e instanceof Button &&
                        Arrays.stream(MathOperation.values())
                                .map(MathOperation::getValue)
                                .collect(Collectors.toList())
                                .contains(((Button) e).getText()))
                .collect(Collectors.toList());
    }
}
