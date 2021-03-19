package pl.piasta.kalkulator.ui.simplemode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import pl.piasta.kalkulator.R;
import pl.piasta.kalkulator.ui.utils.SharedViewModel;

public class SimpleModeFragment extends Fragment {

    private SharedViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_simple_mode, container, false);
        final Button clearButton = root.findViewById(R.id.button_clear);
        final Button clearAllButton = root.findViewById(R.id.button_clear_all);
        final Button switchSignButton = root.findViewById(R.id.button_sign_change);
        clearButton.setOnClickListener(e -> model.clear());
        clearAllButton.setOnClickListener(e -> model.clearAll());
        switchSignButton.setOnClickListener(e -> model.switchSign());
        setNumberButtonsOnClickListener(root.getTouchables());
        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        menu.getItem(0).setChecked(true);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_advanced_mode) {
            NavHostFragment.findNavController(SimpleModeFragment.this)
                    .navigate(R.id.action_SimpleModeFragment_to_AdvancedModeFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNumberButtonsOnClickListener(List<View> touchables) {
        touchables
                .stream()
                .filter(e -> e instanceof Button &&
                        (((Button) e).getText().equals(".") ||
                                IntStream.rangeClosed(0, 9)
                                        .mapToObj(Objects::toString)
                                        .collect(Collectors.toList())
                                        .contains(((Button) e).getText())))
                .forEach(e -> e.setOnClickListener(button -> {
                    model.updateInputValue(((Button) button).getText());
                }));
    }
}