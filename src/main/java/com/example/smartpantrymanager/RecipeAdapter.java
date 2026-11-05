package com.example.smartpantrymanager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class RecipeAdapter extends BaseAdapter {


    // Stores the Android context.
    private Context context;


    // Stores the recipes displayed by the ListView.
    private List<Recipe> recipes;



    public RecipeAdapter(
            Context context, // Receives the current Activity.
            List<Recipe> recipes) { // Receives the recipe data.


        // Stores the Activity context.
        this.context = context;


        // Stores the recipe list.
        this.recipes = recipes;
    }



    @Override
    public int getCount() {


        // Returns the number of records.
        return recipes.size();
    }


    @Override
    public Object getItem(int position) {


        // Returns the recipe at the requested position.
        return recipes.get(position);
    }


    @Override
    public long getItemId(int position) {


        // Returns the database ID.
        return recipes.get(position).getId();
    }


    @Override
    public View getView(
            int position, // Tells us which item is being displayed.
            View convertView, // Reuses an existing row when possible.
            ViewGroup parent) { // Represents the ListView.


        // Checks whether a previous row can be reused.
        if (convertView == null) {


            // Creates the row from item_recipe.xml.
            convertView =
                    LayoutInflater
                            .from(context)
                            .inflate(
                                    R.layout.item_recipe,
                                    parent,
                                    false
                            );
        }


        // Gets the recipe at this position.
        Recipe recipe =
                recipes.get(position);


        // Finds the recipe name TextView.
        TextView recipeNameText =
                convertView.findViewById(
                        R.id.recipeNameText
                );


        // Finds the recipe instructions preview TextView.
        TextView recipeInstructionsText =
                convertView.findViewById(
                        R.id.recipeInstructionsText
                );


        // Displays the recipe name.
        recipeNameText.setText(
                recipe.getName()
        );


        // Displays a short preview of the instructions.
        recipeInstructionsText.setText(
                recipe.getInstructions()
        );


        // Returns the completed row.
        return convertView;
    }
}