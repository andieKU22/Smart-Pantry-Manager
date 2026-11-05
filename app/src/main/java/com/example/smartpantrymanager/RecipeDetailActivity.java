package com.example.smartpantrymanager;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {


    // Stores the database helper.
    private DatabaseHelper databaseHelper;


    /*
     * Runs when the Activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // Calls the parent Activity.
        super.onCreate(savedInstanceState);


        // Loads activity_recipe_detail.xml.
        setContentView(
                R.layout.activity_recipe_detail
        );


        // Creates the database helper.
        databaseHelper =
                new DatabaseHelper(this);


        // Connects the recipe name TextView.
        TextView nameText =
                findViewById(
                        R.id.recipeDetailName
                );


        // Connects the ingredients TextView.
        TextView ingredientsText =
                findViewById(
                        R.id.recipeDetailIngredients
                );


        // Connects the instructions TextView.
        TextView instructionsText =
                findViewById(
                        R.id.recipeDetailInstructions
                );


        // Connects the back button.
        Button backButton =
                findViewById(
                        R.id.backToRecipesButton
                );


        // Closes this screen when Back is clicked.
        backButton.setOnClickListener(
                view -> finish()
        );


        // Gets the Intent that opened this Activity.
        Intent intent = getIntent();


        // Gets the recipe ID passed from the previous screen.
        int recipeId =
                intent.getIntExtra(
                        "recipe_id",
                        -1
                );


        // Checks whether a valid ID was received.
        if (recipeId == -1) {


            // Displays an error message.
            nameText.setText(
                    "Recipe could not be loaded."
            );


            // Stops this method.
            return;
        }


        // Gets all recipes from the database.
        List<Recipe> allRecipes =
                databaseHelper.getAllRecipes();


        // Creates a variable for the selected recipe.
        Recipe selectedRecipe = null;


        // Searches for the recipe with the matching ID.
        for (Recipe recipe : allRecipes) {


            // Checks whether the IDs match.
            if (recipe.getId() == recipeId) {


                // Stores the matching recipe.
                selectedRecipe = recipe;


                // Stops searching.
                break;
            }
        }


        // Checks whether the recipe was found.
        if (selectedRecipe == null) {


            // Displays an error message.
            nameText.setText(
                    "Recipe could not be found."
            );


            // Stops this method.
            return;
        }


        // Displays the recipe name.
        nameText.setText(
                selectedRecipe.getName()
        );


        // Displays the preparation instructions.
        instructionsText.setText(
                selectedRecipe.getInstructions()
        );


        // Gets the required ingredients for this recipe.
        List<RecipeIngredient> ingredients =
                databaseHelper.getRecipeIngredients(
                        recipeId
                );


        // Creates a text builder for the ingredient list.
        StringBuilder ingredientBuilder =
                new StringBuilder();


        // Goes through every required ingredient.
        for (RecipeIngredient ingredient : ingredients) {


            // Adds the ingredient name.
            ingredientBuilder.append("• ");


            // Adds the ingredient name.
            ingredientBuilder.append(
                    ingredient.getName()
            );


            // Adds the quantity.
            ingredientBuilder.append(" - ");


            // Adds the quantity value.
            double qty = ingredient.getQuantity();
            ingredientBuilder.append(
                    qty == Math.floor(qty)
                            ? String.valueOf((long) qty)
                            : String.valueOf(qty)
            );


            // Adds a space.
            ingredientBuilder.append(" ");


            // Adds the unit.
            ingredientBuilder.append(
                    ingredient.getUnit()
            );


            // Moves to the next line.
            ingredientBuilder.append("\n");
        }


        // Displays the ingredient list.
        ingredientsText.setText(
                ingredientBuilder.toString()
        );
    }
}