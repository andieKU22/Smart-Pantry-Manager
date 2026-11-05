package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ListView;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    // Stores the ListView displaying pantry ingredients.
    private ListView pantryListView;


    // Stores the database helper.
    private DatabaseHelper databaseHelper;


    // Stores the custom pantry Adapter.
    private PantryAdapter pantryAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // Calls the parent Activity's onCreate method.
        super.onCreate(savedInstanceState);


        // Loads activity_main.xml onto the screen.
        setContentView(R.layout.activity_main);


        // Creates the database helper.
        databaseHelper =
                new DatabaseHelper(this);


        // Connects the Java variable to the ListView in XML.
        pantryListView =
                findViewById(
                        R.id.pantryListView
                );


        // Finds the Add Ingredient button.
        Button addIngredientButton =
                findViewById(
                        R.id.addIngredientButton
                );


        // Finds the Suggested Recipes button.
        Button suggestedRecipesButton =
                findViewById(
                        R.id.suggestedRecipesButton
                );


        // Finds the Settings button.
        Button settingsButton =
                findViewById(
                        R.id.settingsButton
                );


        // Runs when Add Ingredient is clicked.
        addIngredientButton.setOnClickListener(view -> {


            // Creates an Intent to open AddEditIngredientActivity.
            Intent intent =
                    new Intent(
                            MainActivity.this,
                            AddEditIngredientActivity.class
                    );


            // Opens the Add Ingredient screen.
            startActivity(intent);
        });


        // Runs when Suggested Recipes is clicked.
        suggestedRecipesButton.setOnClickListener(view -> {


            // Creates an Intent for SuggestedRecipesActivity.
            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SuggestedRecipesActivity.class
                    );


            // Opens the Suggested Recipes screen.
            startActivity(intent);
        });


        // Runs when Settings is clicked.
        settingsButton.setOnClickListener(view -> {


            // Creates an Intent for SettingsActivity.
            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );


            // Opens the Settings screen.
            startActivity(intent);
        });


        // Loads the current pantry records.
        loadPantry();
    }



    @Override
    protected void onResume() {


        // Calls the parent implementation.
        super.onResume();


        // Reloads the pantry after returning from another screen.
        loadPantry();
    }



    private void loadPantry() {


        // Gets all pantry records from SQLite.
        List<PantryItem> items =
                databaseHelper.getAllPantryItems();


        // Creates the custom Adapter.
        pantryAdapter =
                new PantryAdapter(
                        this,
                        items
                );


        // Connects the Adapter to the ListView.
        pantryListView.setAdapter(
                pantryAdapter
        );
    }
}