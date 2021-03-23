package pl.piasta.kalkulator.ui.advancedmode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;

import pl.piasta.kalkulator.R;
import pl.piasta.kalkulator.ui.SharedViewModel;
import pl.piasta.kalkulator.ui.utils.FragmentUtils;
import pl.piasta.kalkulator.ui.utils.MathOperation;

public class AdvancedModeFragment extends Fragment {

    private SharedViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_advanced_mode, container, false);
        final Button clearButton = root.findViewById(R.id.button_clear);
        final Button clearAllButton = root.findViewById(R.id.button_clear_all);
        final Button switchSignButton = root.findViewById(R.id.button_sign_change);
        final Button percentageButton = root.findViewById(R.id.button_percentage);
        final List<View> touchables = root.getTouchables();
        clearButton.setOnClickListener(e -> model.clear());
        clearAllButton.setOnClickListener(e -> model.clearAll());
        switchSignButton.setOnClickListener(e -> model.switchSign());
        percentageButton.setOnClickListener(e -> model.convertToPercentage());
        setNumberButtonsOnClickListener(touchables);
        setMathOperationButtonsOnClickListener(touchables);
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        menu.getItem(1).setChecked(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_simple_mode) {
            NavHostFragment.findNavController(AdvancedModeFragment.this)
                    .navigate(R.id.action_AdvancedModeFragment_to_SimpleModeFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNumberButtonsOnClickListener(List<View> touchables) {
        FragmentUtils.getNumberButtons(touchables)
                .forEach(e -> e.setOnClickListener(button -> {
                    model.updateInputValue(((Button) button).getText().toString());
                }));
    }

    private void setMathOperationButtonsOnClickListener(List<View> touchables) {
        FragmentUtils.getMathOperationButtons(touchables)
                .forEach(e -> e.setOnClickListener(button -> {
                    String value = ((TextView) requireActivity().findViewById(R.id.input_value))
                            .getText().toString();
                    MathOperation operation = MathOperation.findByValue(((Button) button)
                            .getText().toString());
                    model.readMathOperation(value, operation);
                }));
    }
}