package pl.piasta.kalkulator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import pl.piasta.kalkulator.ui.utils.SharedViewModel;

public class MainActivity extends AppCompatActivity {

    private TextView inputValue;
    private HorizontalScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final SharedViewModel model = new ViewModelProvider(this).get(SharedViewModel.class);
        inputValue = findViewById(R.id.input_value);
        scrollView = findViewById(R.id.horizontal_scroll);
        model.getInputValue().observe(this, this::updateInputValue);
    }

    private void updateInputValue(String value) {
        inputValue.setText(value);
        inputValue.post(() -> scrollView.fullScroll(View.FOCUS_RIGHT));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            createAboutDialog();
            return true;
        } else if (id == R.id.action_exit) {
            finishAndRemoveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createAboutDialog() {
        new MaterialAlertDialogBuilder(this, R.style.Theme_Kalkulator_Dialog)
                .setTitle("O aplikacji")
                .setMessage(createAboutDialogMessage())
                .show();
    }

    private String createAboutDialogMessage() {
        return "Nazwa aplikacji: Kalkulator" + System.lineSeparator() +
                "Autor: Kacper Piasta 222537" + System.lineSeparator() +
                "Informacje: Projektowanie Aplikacji Mobilnych 2021";
    }
}