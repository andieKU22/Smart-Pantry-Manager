package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {


    // Stores the expiring-soon alerts toggle.
    private Switch expiryAlertSwitch;


    // Stores the preferred units dropdown.
    private Spinner preferredUnitsSpinner;


    // Stores the SharedPreferences file name.
    private static final String PREFS_NAME = "smart_pantry_settings";


    /*
     * Runs when the Activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // Calls the parent Activity.
        super.onCreate(savedInstanceState);


        // Loads activity_settings.xml.
        setContentView(
                R.layout.activity_settings
        );


        // Connects the alerts toggle.
        expiryAlertSwitch =
                findViewById(
                        R.id.expiryAlertSwitch
                );


        // Connects the units Spinner.
        preferredUnitsSpinner =
                findViewById(
                        R.id.preferredUnitsSpinner
                );


        // Connects the Save button.
        Button saveButton =
                findViewById(
                        R.id.saveSettingsButton
                );


        // Connects the Back button.
        Button backButton =
                findViewById(
                        R.id.backToPantryButton
                );


        // Creates the list of unit preferences.
        String[] unitOptions = {
                "Metric (g, kg, ml, l)",
                "Imperial (tsp, tbsp, pieces)"
        };


        // Creates an Adapter for the Spinner.
        ArrayAdapter<String> unitsAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        unitOptions
                );


        // Sets the layout used when the Spinner is opened.
        unitsAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        // Connects the Adapter to the Spinner.
        preferredUnitsSpinner.setAdapter(
                unitsAdapter
        );


        // Loads previously saved settings.
        loadSettings();


        // Runs when Save is clicked.
        saveButton.setOnClickListener(
                view -> saveSettings()
        );


        // Closes this screen when Back is clicked.
        backButton.setOnClickListener(
                view -> finish()
        );
    }


    /*
     * Loads saved settings from SharedPreferences.
     */
    private void loadSettings() {


        // Opens the settings file.
        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );


        // Reads the alerts setting.
        boolean alertsEnabled =
                preferences.getBoolean(
                        "expiry_alerts",
                        true
                );


        // Reads the units preference.
        int unitsIndex =
                preferences.getInt(
                        "preferred_units",
                        0
                );


        // Applies the alerts setting.
        expiryAlertSwitch.setChecked(
                alertsEnabled
        );


        // Applies the units setting.
        preferredUnitsSpinner.setSelection(
                unitsIndex
        );
    }


    /*
     * Saves the current settings to SharedPreferences.
     */
    private void saveSettings() {


        // Opens the settings file for writing.
        SharedPreferences preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );


        // Starts an edit operation.
        SharedPreferences.Editor editor =
                preferences.edit();


        // Saves the alerts setting.
        editor.putBoolean(
                "expiry_alerts",
                expiryAlertSwitch.isChecked()
        );


        // Saves the units setting.
        editor.putInt(
                "preferred_units",
                preferredUnitsSpinner
                        .getSelectedItemPosition()
        );


        // Applies the changes.
        editor.apply();


        // Displays a confirmation message.
        Toast.makeText(
                this,
                "Settings saved.",
                Toast.LENGTH_SHORT
        ).show();
    }
}