package com.example.smartpantrymanager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;



public class SuggestedRecipesActivity extends AppCompatActivity {


    // Stores the ListView displaying matching recipes.
    private ListView recipeListView;


    // Stores the empty-state TextView.
    private TextView noRecipesText;


    // Stores the database helper.
    private DatabaseHelper databaseHelper;


    /*
     * Runs when the Activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // Calls the parent Activity.
        super.onCreate(savedInstanceState);


        // Loads activity_suggested_recipes.xml.
        setContentView(
                R.layout.activity_suggested_recipes
        );


        // Creates the database helper.
        databaseHelper =
                new DatabaseHelper(this);


        // Connects the ListView.
        recipeListView =
                findViewById(
                        R.id.recipeListView
                );


        // Connects the empty-state TextView.
        noRecipesText =
                findViewById(
                        R.id.noRecipesText
                );


        // Connects the back button.
        Button backButton =
                findViewById(
                        R.id.backButton
                );


        // Closes this screen when Back is clicked.
        backButton.setOnClickListener(
                view -> finish()
        );


        // Loads the suggested recipes.
        loadSuggestedRecipes();
    }



    private void loadSuggestedRecipes() {


        // Gets every recipe from the database.
        List<Recipe> allRecipes =
                databaseHelper.getAllRecipes();


        // Creates a list to hold only matching recipes.
        List<Recipe> matchingRecipes =
                new ArrayList<>();


        // Goes through every recipe.
        for (Recipe recipe : allRecipes) {


            // Checks the strict-matching rule.
            if (databaseHelper.canMakeRecipe(
                    recipe.getId()
            )) {


                // Adds the recipe because every ingredient exists.
                matchingRecipes.add(recipe);
            }
        }


        // Checks whether any recipes matched.
        if (matchingRecipes.isEmpty()) {


            // Hides the ListView.
            recipeListView.setVisibility(
                    android.view.View.GONE
            );


            // Shows the empty-state message.
            noRecipesText.setVisibility(
                    android.view.View.VISIBLE
            );


        } else {


            // Shows the ListView.
            recipeListView.setVisibility(
                    android.view.View.VISIBLE
            );


            // Hides the empty-state message.
            noRecipesText.setVisibility(
                    android.view.View.GONE
            );


            // Creates the custom recipe Adapter.
            RecipeAdapter adapter =
                    new RecipeAdapter(
                            this,
                            matchingRecipes
                    );


            // Connects the Adapter to the ListView.
            recipeListView.setAdapter(adapter);


            // Opens the Recipe Detail screen when a recipe is clicked.
            recipeListView.setOnItemClickListener(
                    (parent, view, position, id) -> {


                        // Gets the selected recipe.
                        Recipe selectedRecipe =
                                matchingRecipes.get(position);


                        // Creates an Intent for RecipeDetailActivity.
                        android.content.Intent intent =
                                new android.content.Intent(
                                        SuggestedRecipesActivity.this,
                                        RecipeDetailActivity.class
                                );


                        // Sends the recipe ID to the detail screen.
                        intent.putExtra(
                                "recipe_id",
                                selectedRecipe.getId()
                        );


                        // Opens the detail screen.
                        startActivity(intent);
                    }
            );
        }
    }
}