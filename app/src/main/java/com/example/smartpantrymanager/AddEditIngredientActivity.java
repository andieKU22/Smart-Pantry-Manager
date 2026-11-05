package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;



public class AddEditIngredientActivity
        extends AppCompatActivity {


    // Stores the ingredient name input field.
    private EditText ingredientNameInput;


    // Stores the quantity input field.
    private EditText quantityInput;


    // Stores the unit dropdown.
    private Spinner unitSpinner;


    // Stores the expiry date input.
    private EditText expiryDateInput;


    // Stores the screen title.
    private android.widget.TextView formTitle;


    // Stores the database helper.
    private DatabaseHelper databaseHelper;


    // Stores the ID being edited.
    private int pantryId = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // Calls the parent Activity.
        super.onCreate(savedInstanceState);


        // Loads the XML layout.
        setContentView(
                R.layout.activity_add_edit_ingredient
        );


        // Creates the database helper.
        databaseHelper =
                new DatabaseHelper(this);


        // Connects the name input.
        ingredientNameInput =
                findViewById(
                        R.id.ingredientNameInput
                );


        // Connects the quantity input.
        quantityInput =
                findViewById(
                        R.id.quantityInput
                );


        // Connects the unit Spinner.
        unitSpinner =
                findViewById(
                        R.id.unitSpinner
                );


        // Connects the expiry date input.
        expiryDateInput =
                findViewById(
                        R.id.expiryDateInput
                );


        // Connects the title.
        formTitle =
                findViewById(
                        R.id.formTitle
                );


        // Connects the Save button.
        Button saveButton =
                findViewById(
                        R.id.saveIngredientButton
                );


        // Connects the Cancel button.
        Button cancelButton =
                findViewById(
                        R.id.cancelButton
                );


        // Creates the list of measurement units.
        String[] units = {
                "pieces",
                "g",
                "kg",
                "ml",
                "l",
                "tsp",
                "tbsp"
        };


        // Creates an Adapter for the Spinner.
        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );


        // Sets the layout used when the Spinner is opened.
        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        // Connects the Adapter to the Spinner.
        unitSpinner.setAdapter(
                unitAdapter
        );


        // Checks whether this Activity was opened for editing.
        Intent intent = getIntent();


        // Checks whether an ID was passed from the Pantry Adapter.
        if (intent.hasExtra("pantry_id")) {


            // Gets the pantry ID.
            pantryId =
                    intent.getIntExtra(
                            "pantry_id",
                            -1
                    );


            // Changes the title to Edit Ingredient.
            formTitle.setText(
                    "Edit Ingredient"
            );


            // Loads the existing ingredient.
            loadExistingItem();
        }


        // Runs when Save is clicked.
        saveButton.setOnClickListener(
                view -> saveIngredient()
        );


        // Runs when Cancel is clicked.
        cancelButton.setOnClickListener(
                view -> finish()
        );
    }



    private void loadExistingItem() {


        // Gets the item from SQLite.
        PantryItem item =
                databaseHelper.getPantryItem(
                        pantryId
                );


        // Checks whether the item exists.
        if (item == null) {


            // Displays an error message.
            Toast.makeText(
                    this,
                    "Ingredient could not be found.",
                    Toast.LENGTH_SHORT
            ).show();


            // Closes this screen.
            finish();


            // Stops this method.
            return;
        }


        // Places the ingredient name into the input.
        ingredientNameInput.setText(
                item.getName()
        );


        // Places the quantity into the input.
        quantityInput.setText(
                String.valueOf(
                        item.getQuantity()
                )
        );


        // Places the expiry date into the input.
        if (item.getExpiryDate() != null) {


            expiryDateInput.setText(
                    item.getExpiryDate()
            );
        }


        // Gets the current unit.
        String currentUnit =
                item.getUnit();


        // Searches through the Spinner options.
        for (int i = 0;
             i < unitSpinner.getCount();
             i++) {


            // Gets the Spinner unit.
            String spinnerUnit =
                    unitSpinner
                            .getItemAtPosition(i)
                            .toString();


            // Checks whether the unit matches.
            if (spinnerUnit.equals(
                    currentUnit
            )) {


                // Selects the matching unit.
                unitSpinner.setSelection(i);


                // Stops searching.
                break;
            }
        }
    }



    private void saveIngredient() {


        // Gets the entered ingredient name.
        String name =
                ingredientNameInput
                        .getText()
                        .toString()
                        .trim();


        // Gets the entered quantity.
        String quantityText =
                quantityInput
                        .getText()
                        .toString()
                        .trim();


        // Gets the selected unit.
        String unit =
                unitSpinner
                        .getSelectedItem()
                        .toString();


        // Gets the optional expiry date.
        String expiryDate =
                expiryDateInput
                        .getText()
                        .toString()
                        .trim();





        // Checks whether the ingredient name is empty.
        if (name.isEmpty()) {


            // Displays an error under the name field.
            ingredientNameInput.setError(
                    "Ingredient name is required."
            );


            // Stops the save operation.
            return;
        }


        // Checks whether quantity is empty.
        if (quantityText.isEmpty()) {


            // Displays an error under quantity.
            quantityInput.setError(
                    "Quantity is required."
            );


            // Stops the save operation.
            return;
        }


        // Converts the quantity from text to a number.
        double quantity;


        // Attempts to convert the quantity.
        try {


            // Converts the text to a double.
            quantity =
                    Double.parseDouble(
                            quantityText
                    );


        } catch (NumberFormatException exception) {


            // Displays an error if conversion fails.
            quantityInput.setError(
                    "Enter a valid number."
            );


            // Stops the save operation.
            return;
        }


        // Checks that quantity is greater than zero.
        if (quantity <= 0) {


            // Displays an error.
            quantityInput.setError(
                    "Quantity must be greater than zero."
            );


            // Stops the save operation.
            return;
        }




        // Checks that the optional expiry date uses the YYYY-MM-DD format.
        if (!expiryDate.isEmpty()) {

            try {

                // Parses the date strictly.
                java.text.SimpleDateFormat format =
                        new java.text.SimpleDateFormat(
                                "yyyy-MM-dd",
                                java.util.Locale.US
                        );
                format.setLenient(false);
                format.parse(expiryDate);

            } catch (java.text.ParseException exception) {

                // Displays an error under the expiry date field.
                expiryDateInput.setError(
                        "Use the format YYYY-MM-DD."
                );

                // Stops the save operation.
                return;
            }
        }


        // Checks whether this is a new ingredient.
        if (pantryId == -1) {


            // Inserts the new ingredient into SQLite.
            databaseHelper.addPantryItem(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );


            // Displays a success message.
            Toast.makeText(
                    this,
                    "Ingredient added.",
                    Toast.LENGTH_SHORT
            ).show();


        } else {


            // Updates the existing ingredient.
            databaseHelper.updatePantryItem(
                    pantryId,
                    name,
                    quantity,
                    unit,
                    expiryDate
            );


            // Displays a success message.
            Toast.makeText(
                    this,
                    "Ingredient updated.",
                    Toast.LENGTH_SHORT
            ).show();
        }


        // Returns to the Pantry screen.
        finish();
    }
}