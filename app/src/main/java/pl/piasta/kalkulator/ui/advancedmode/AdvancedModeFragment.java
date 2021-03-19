package pl.piasta.kalkulator.ui.advancedmode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import pl.piasta.kalkulator.R;
import pl.piasta.kalkulator.ui.utils.SharedViewModel;

public class AdvancedModeFragment extends Fragment {

    private SharedViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState
    ) {
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_advanced_mode, container, false);
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
}