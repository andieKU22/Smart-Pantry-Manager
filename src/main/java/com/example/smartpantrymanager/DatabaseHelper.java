package com.example.smartpantrymanager; // Defines the package for this database class.

// Imports ContentValues so we can place values into database rows.
import android.content.ContentValues;

// Imports Context so the database can be connected to the Android application.
import android.content.Context;

// Imports Cursor so we can read information returned from SQLite.
import android.database.Cursor;

// Imports SQLiteDatabase so we can create and manipulate the database.
import android.database.sqlite.SQLiteDatabase;

// Imports SQLiteOpenHelper which helps create and manage SQLite databases.
import android.database.sqlite.SQLiteOpenHelper;

// Imports file and stream classes used to copy the pre-built database from assets.
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// Imports ArrayList so we can store multiple database objects.
import java.util.ArrayList;

// Imports List so methods can return lists of objects.
import java.util.List;



public class DatabaseHelper extends SQLiteOpenHelper {


    // Stores the name of the database file.
    private static final String DATABASE_NAME = "smart_pantry.db";


    // Stores the current version number of the database.
    private static final int DATABASE_VERSION = 1;



    // Stores the application context used to find the database file and the assets folder.
    private final Context appContext;



    public DatabaseHelper(Context context) {

        // Calls SQLiteOpenHelper and gives it the database details.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        // Keeps the application context.
        this.appContext = context.getApplicationContext();

        // Copies the pre-built database (app/src/main/assets/smart_pantry.db)
        // to the device the first time the app runs.
        copyDatabaseFromAssetsIfNeeded();
    }



    /*
     * Copies the ready-made SQLite database from the assets folder into the
     * app's private databases folder, but only when it does not exist yet.
     * If the copy fails, SQLiteOpenHelper simply creates the database with
     * onCreate() instead, so the app still works.
     */
    private void copyDatabaseFromAssetsIfNeeded() {

        // Finds where Android expects the database file to be.
        File databaseFile = appContext.getDatabasePath(DATABASE_NAME);

        // Does nothing when the database already exists (keeps the user's data).
        if (databaseFile.exists()) {
            return;
        }

        try {

            // Makes sure the databases folder exists.
            File parent = databaseFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            // Opens the pre-built database in assets and the destination file.
            try (InputStream input = appContext.getAssets().open(DATABASE_NAME);
                 OutputStream output = new FileOutputStream(databaseFile)) {

                // Copies the file in small blocks.
                byte[] buffer = new byte[4096];
                int length;
                while ((length = input.read(buffer)) > 0) {
                    output.write(buffer, 0, length);
                }

                // Makes sure everything is written to the file.
                output.flush();
            }

        } catch (IOException exception) {

            // Removes a half-copied file so onCreate() can build a fresh database.
            if (databaseFile.exists()) {
                databaseFile.delete();
            }
        }
    }



    @Override // Runs every time the database is opened.
    public void onConfigure(SQLiteDatabase db) {

        // Calls the parent implementation.
        super.onConfigure(db);

        // Turns on foreign key checking so recipe_ingredients stays linked to recipes.
        db.setForeignKeyConstraintsEnabled(true);
    }



    @Override // Tells Java that we are overriding SQLiteOpenHelper's method.
    public void onCreate(SQLiteDatabase db) { // Receives the newly created database.


        // Creates the pantry table.
        db.execSQL(
                "CREATE TABLE pantry (" +

                        // Creates an automatically increasing unique ID.
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                        // Stores the ingredient name.
                        "name TEXT NOT NULL," +

                        // Stores the quantity.
                        "quantity REAL NOT NULL," +

                        // Stores the unit.
                        "unit TEXT NOT NULL," +

                        // Stores the optional expiry date.
                        "expiry_date TEXT" +

                        // Finishes the CREATE TABLE statement.
                        ")"
        );


        // Creates the recipes table.
        db.execSQL(
                "CREATE TABLE recipes (" +

                        // Creates the unique recipe ID.
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                        // Stores the recipe name.
                        "name TEXT NOT NULL," +

                        // Stores the recipe preparation instructions.
                        "instructions TEXT NOT NULL" +

                        // Finishes the CREATE TABLE statement.
                        ")"
        );


        // Creates the recipe ingredients table.
        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +

                        // Creates a unique ID for this record.
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +

                        // Stores the ID of the recipe.
                        "recipe_id INTEGER NOT NULL," +

                        // Stores the ingredient required by the recipe.
                        "ingredient_name TEXT NOT NULL," +

                        // Stores how much is required.
                        "quantity REAL NOT NULL," +

                        // Stores the required unit.
                        "unit TEXT NOT NULL," +

                        // Links recipe_id to the recipes table.
                        "FOREIGN KEY(recipe_id) REFERENCES recipes(id)" +

                        // Finishes the CREATE TABLE statement.
                        ")"
        );


        // Adds the initial recipes inside one transaction (faster and all-or-nothing).
        db.beginTransaction();
        try {
            seedRecipes(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }



    @Override // Overrides the SQLiteOpenHelper method.
    public void onUpgrade(
            SQLiteDatabase db, // The database being upgraded.
            int oldVersion, // The previous database version.
            int newVersion) { // The new database version.


        // Deletes the recipe ingredients table if it exists.
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");


        // Deletes the recipes table if it exists.
        db.execSQL("DROP TABLE IF EXISTS recipes");


        // Deletes the pantry table if it exists.
        db.execSQL("DROP TABLE IF EXISTS pantry");


        // Recreates all tables.
        onCreate(db);
    }




    public long addPantryItem(
            String name, // Receives the ingredient name.
            double quantity, // Receives the quantity.
            String unit, // Receives the measurement unit.
            String expiryDate) { // Receives the optional expiry date.


        // Opens the database so that we can write information.
        SQLiteDatabase db = getWritableDatabase();


        // Creates a ContentValues object to hold the new row.
        ContentValues values = new ContentValues();


        // Places the ingredient name into the row.
        values.put("name", name);


        // Places the quantity into the row.
        values.put("quantity", quantity);


        // Places the unit into the row.
        values.put("unit", unit);


        // Places the expiry date into the row.
        values.put("expiry_date", expiryDate);


        // Inserts the new row into the pantry table.
        return db.insert("pantry", null, values);
    }



    public List<PantryItem> getAllPantryItems() {


        // Creates an empty list to hold pantry items.
        List<PantryItem> items = new ArrayList<>();


        // Opens the database in read mode.
        SQLiteDatabase db = getReadableDatabase();


        // Runs an SQL query to retrieve all pantry items.
        Cursor cursor = db.rawQuery(
                "SELECT id, name, quantity, unit, expiry_date " +
                        "FROM pantry ORDER BY name",
                null
        );


        // Checks whether the query returned at least one row.
        if (cursor.moveToFirst()) {


            // Continues through every row.
            do {


                // Gets the ID from column 0.
                int id = cursor.getInt(0);


                // Gets the name from column 1.
                String name = cursor.getString(1);


                // Gets the quantity from column 2.
                double quantity = cursor.getDouble(2);


                // Gets the unit from column 3.
                String unit = cursor.getString(3);


                // Gets the expiry date from column 4.
                String expiryDate = cursor.getString(4);


                // Creates a PantryItem object using the database information.
                PantryItem item = new PantryItem(
                        id,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );


                // Adds the object to the list.
                items.add(item);


                // Moves to the next database row.
            } while (cursor.moveToNext());
        }


        // Closes the cursor after reading the database.
        cursor.close();


        // Returns the complete pantry list.
        return items;
    }



    public PantryItem getPantryItem(int id) {


        // Opens the database for reading.
        SQLiteDatabase db = getReadableDatabase();


        // Searches for a row with the requested ID.
        Cursor cursor = db.rawQuery(
                "SELECT id, name, quantity, unit, expiry_date " +
                        "FROM pantry WHERE id = ?",
                new String[]{String.valueOf(id)}
        );


        // Checks whether an item was found.
        if (cursor.moveToFirst()) {


            // Creates a PantryItem from the database row.
            PantryItem item = new PantryItem(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4)
            );


            // Closes the cursor.
            cursor.close();


            // Returns the item.
            return item;
        }


        // Closes the cursor when nothing was found.
        cursor.close();


        // Returns null because no item exists with this ID.
        return null;
    }



    public int updatePantryItem(
            int id, // Receives the ID to update.
            String name, // Receives the new ingredient name.
            double quantity, // Receives the new quantity.
            String unit, // Receives the new unit.
            String expiryDate) { // Receives the new expiry date.


        // Opens the database for writing.
        SQLiteDatabase db = getWritableDatabase();


        // Creates a container for the new values.
        ContentValues values = new ContentValues();


        // Stores the updated name.
        values.put("name", name);


        // Stores the updated quantity.
        values.put("quantity", quantity);


        // Stores the updated unit.
        values.put("unit", unit);


        // Stores the updated expiry date.
        values.put("expiry_date", expiryDate);


        // Updates the database row with the matching ID.
        return db.update(
                "pantry",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }


    public int deletePantryItem(int id) {


        // Opens the database for writing.
        SQLiteDatabase db = getWritableDatabase();


        // Deletes the row with the specified ID.
        return db.delete(
                "pantry",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }



    public List<Recipe> getAllRecipes() {


        // Creates an empty recipe list.
        List<Recipe> recipes = new ArrayList<>();


        // Opens the database for reading.
        SQLiteDatabase db = getReadableDatabase();


        // Gets all recipes ordered alphabetically.
        Cursor cursor = db.rawQuery(
                "SELECT id, name, instructions " +
                        "FROM recipes ORDER BY name",
                null
        );


        // Checks whether at least one recipe exists.
        if (cursor.moveToFirst()) {


            // Loops through every recipe.
            do {


                // Creates a Recipe object.
                Recipe recipe = new Recipe(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );


                // Adds the recipe to the list.
                recipes.add(recipe);


                // Moves to the next recipe.
            } while (cursor.moveToNext());
        }


        // Closes the cursor.
        cursor.close();


        // Returns all recipes.
        return recipes;
    }



    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {


        // Creates an empty list.
        List<RecipeIngredient> ingredients = new ArrayList<>();


        // Opens the database.
        SQLiteDatabase db = getReadableDatabase();


        // Searches for ingredients belonging to the selected recipe.
        Cursor cursor = db.rawQuery(
                "SELECT ingredient_name, quantity, unit " +
                        "FROM recipe_ingredients " +
                        "WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );


        // Checks if ingredients were found.
        if (cursor.moveToFirst()) {


            // Loops through every ingredient.
            do {


                // Creates a RecipeIngredient object.
                RecipeIngredient ingredient =
                        new RecipeIngredient(
                                cursor.getString(0),
                                cursor.getDouble(1),
                                cursor.getString(2)
                        );


                // Adds the ingredient to the list.
                ingredients.add(ingredient);


                // Moves to the next ingredient.
            } while (cursor.moveToNext());
        }


        // Closes the cursor.
        cursor.close();


        // Returns all required ingredients.
        return ingredients;
    }



    public boolean canMakeRecipe(int recipeId) {


        // Gets every ingredient required by the recipe.
        List<RecipeIngredient> requiredIngredients =
                getRecipeIngredients(recipeId);


        // Gets every ingredient currently in the pantry.
        List<PantryItem> pantryItems =
                getAllPantryItems();


        // Goes through every ingredient required by the recipe.
        for (RecipeIngredient required : requiredIngredients) {


            // Converts the recipe ingredient name to a standard format.
            String requiredName =
                    normaliseIngredientName(
                            required.getName()
                    );


            // Adds up every pantry row of this ingredient (e.g. two separate "Egg" entries).
            double totalAvailable = 0;


            // Checks every pantry item.
            for (PantryItem pantry : pantryItems) {


                // Converts the pantry ingredient name to a standard format.
                String pantryName =
                        normaliseIngredientName(
                                pantry.getName()
                        );


                // Checks whether the names match.
                if (pantryName.equals(requiredName)) {


                    // Converts the pantry quantity to the recipe's unit.
                    double converted =
                            convertQuantity(
                                    pantry.getQuantity(),
                                    pantry.getUnit(),
                                    required.getUnit()
                            );


                    // Only counts it when the units could be compared.
                    if (converted >= 0) {

                        // Adds this pantry row to the total.
                        totalAvailable += converted;
                    }
                }
            }


            // If there is not enough of even one required ingredient...
            if (totalAvailable < required.getQuantity()) {


                // The recipe is NOT allowed to appear.
                return false;
            }
        }


        // Every required ingredient was found in sufficient quantity.
        return true;
    }



    private String normaliseIngredientName(String name) {


        // Converts the ingredient to lowercase and removes extra spaces.
        String result = name.toLowerCase().trim();


        // Handles plurals such as "tomatoes" and "potatoes" -> "tomato" and "potato".
        if (result.endsWith("oes") && result.length() > 3) {

            // Removes the final "es".
            result = result.substring(0, result.length() - 2);


        // Handles common plural words ending in "s".
        } else if (result.endsWith("s") &&
                !result.endsWith("ss")) {

            // Removes the final "s".
            result = result.substring(0, result.length() - 1);
        }


        // Returns the cleaned ingredient name.
        return result;
    }


    /*
     * Makes unit names consistent, e.g. "pieces" and "pieces" are the same unit.
     */
    private String normaliseUnit(String unit) {

        // Lowercases and trims the unit.
        String result = unit.toLowerCase().trim();

        // Treats "pieces" and "pieces" as the same unit.
        if (result.equals("pieces")) {
            return "pieces";
        }

        // Returns the cleaned unit.
        return result;
    }


    /*
     * Converts common measurement units.
     */
    private double convertQuantity(
            double quantity, // The quantity currently in the pantry.
            String fromUnit, // The pantry unit.
            String toUnit) { // The recipe's required unit.


        // Converts both units to lowercase.
        String from = normaliseUnit(fromUnit);


        // Converts the destination unit to lowercase.
        String to = normaliseUnit(toUnit);


        // If both units are identical...
        if (from.equals(to)) {


            // No conversion is required.
            return quantity;
        }


        // Converts kilograms into grams.
        if (from.equals("kg") &&
                to.equals("g")) {


            // One kilogram equals 1000 grams.
            return quantity * 1000;
        }


        // Converts grams into kilograms.
        if (from.equals("g") &&
                to.equals("kg")) {


            // Divides grams by 1000.
            return quantity / 1000;
        }


        // Converts litres into millilitres.
        if (from.equals("l") &&
                to.equals("ml")) {


            // One litre equals 1000 millilitres.
            return quantity * 1000;
        }


        // Converts millilitres into litres.
        if (from.equals("ml") &&
                to.equals("l")) {


            // Divides millilitres by 1000.
            return quantity / 1000;
        }


        // Converts tablespoons into teaspoons.
        if (from.equals("tbsp") &&
                to.equals("tsp")) {


            // One tablespoon equals three teaspoons.
            return quantity * 3;
        }


        // Converts teaspoons into tablespoons.
        if (from.equals("tsp") &&
                to.equals("tbsp")) {


            // Divides teaspoons by three.
            return quantity / 3;
        }


        // Returns -1 when the units cannot safely be compared.
        return -1;
    }


    // ============================================================
    // RECIPE SEEDING
    // ============================================================


    /*
     * Adds the initial recipe collection.
     *
     * The assignment requires at least 15-20 recipes.
     * We will create 20.
     */
    private void seedRecipes(SQLiteDatabase db) {


        // Adds recipe 1.
        addRecipe(
                db,
                "Tomato Pasta",
                "Cook pasta. Fry tomatoes and garlic. " +
                        "Mix together and serve.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "tomato",
                                3,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "pasta",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "garlic",
                                2,
                                "pieces"
                        )
                }
        );


        // Adds recipe 2.
        addRecipe(
                db,
                "Egg Sandwich",
                "Cook the eggs. Place them between bread " +
                        "and add tomato.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "egg",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "bread",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "tomato",
                                1,
                                "pieces"
                        )
                }
        );


        // Adds recipe 3.
        addRecipe(
                db,
                "Pancakes",
                "Mix flour, milk, egg and sugar. " +
                        "Cook the mixture in a pan.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "flour",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "milk",
                                250,
                                "ml"
                        ),
                        new RecipeIngredient(
                                "egg",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "sugar",
                                30,
                                "g"
                        )
                }
        );


        // Adds recipe 4.
        addRecipe(
                db,
                "Cheese Omelette",
                "Beat eggs. Cook in a pan and add cheese.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "egg",
                                3,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "cheese",
                                50,
                                "g"
                        )
                }
        );


        // Adds recipe 5.
        addRecipe(
                db,
                "Chicken Rice",
                "Cook chicken and rice. Combine with onion.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "chicken",
                                250,
                                "g"
                        ),
                        new RecipeIngredient(
                                "rice",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "onion",
                                1,
                                "pieces"
                        )
                }
        );


        // Adds recipe 6.
        addRecipe(
                db,
                "Garlic Bread",
                "Spread butter and garlic on bread. Toast until golden.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "bread",
                                4,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "butter",
                                30,
                                "g"
                        ),
                        new RecipeIngredient(
                                "garlic",
                                2,
                                "pieces"
                        )
                }
        );


        // Adds recipe 7.
        addRecipe(
                db,
                "French Toast",
                "Dip bread into beaten eggs and milk. Fry until golden.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "bread",
                                3,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "egg",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "milk",
                                100,
                                "ml"
                        )
                }
        );


        // Adds recipe 8.
        addRecipe(
                db,
                "Chicken Sandwich",
                "Cook chicken and place it inside bread with tomato.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "chicken",
                                150,
                                "g"
                        ),
                        new RecipeIngredient(
                                "bread",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "tomato",
                                1,
                                "pieces"
                        )
                }
        );


        // Adds recipe 9.
        addRecipe(
                db,
                "Rice and Beans",
                "Cook rice and beans together with onion.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "rice",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "beans",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "onion",
                                1,
                                "pieces"
                        )
                }
        );


        // Adds recipe 10.
        addRecipe(
                db,
                "Tomato Soup",
                "Cook tomatoes with onion and garlic. Blend until smooth.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "tomato",
                                5,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "onion",
                                1,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "garlic",
                                2,
                                "pieces"
                        )
                }
        );


        // Adds recipe 11.
        addRecipe(
                db,
                "Cheese Pasta",
                "Cook pasta and mix with cheese and milk.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "pasta",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "cheese",
                                100,
                                "g"
                        ),
                        new RecipeIngredient(
                                "milk",
                                100,
                                "ml"
                        )
                }
        );


        // Adds recipe 12.
        addRecipe(
                db,
                "Scrambled Eggs",
                "Beat eggs with milk and cook gently.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "egg",
                                3,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "milk",
                                50,
                                "ml"
                        )
                }
        );


        // Adds recipe 13.
        addRecipe(
                db,
                "Chicken Pasta",
                "Cook pasta and chicken. Mix together with cheese.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "chicken",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "pasta",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "cheese",
                                50,
                                "g"
                        )
                }
        );


        // Adds recipe 14.
        addRecipe(
                db,
                "Vegetable Rice",
                "Cook rice with vegetables and onion.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "rice",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "vegetables",
                                150,
                                "g"
                        ),
                        new RecipeIngredient(
                                "onion",
                                1,
                                "pieces"
                        )
                }
        );


        // Adds recipe 15.
        addRecipe(
                db,
                "Banana Pancakes",
                "Mash banana. Mix with flour, egg and milk.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "banana",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "flour",
                                150,
                                "g"
                        ),
                        new RecipeIngredient(
                                "egg",
                                1,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "milk",
                                150,
                                "ml"
                        )
                }
        );


        // Adds recipe 16.
        addRecipe(
                db,
                "Cheese Sandwich",
                "Place cheese between bread and toast.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "bread",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "cheese",
                                50,
                                "g"
                        )
                }
        );


        // Adds recipe 17.
        addRecipe(
                db,
                "Potato Omelette",
                "Cook potatoes and combine with beaten eggs.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "potato",
                                3,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "egg",
                                3,
                                "pieces"
                        )
                }
        );


        // Adds recipe 18.
        addRecipe(
                db,
                "Chicken Soup",
                "Cook chicken with vegetables and onion.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "chicken",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "vegetables",
                                150,
                                "g"
                        ),
                        new RecipeIngredient(
                                "onion",
                                1,
                                "pieces"
                        )
                }
        );


        // Adds recipe 19.
        addRecipe(
                db,
                "Tomato Cheese Toast",
                "Place tomato and cheese on bread and toast.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "bread",
                                2,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "tomato",
                                1,
                                "pieces"
                        ),
                        new RecipeIngredient(
                                "cheese",
                                50,
                                "g"
                        )
                }
        );


        // Adds recipe 20.
        addRecipe(
                db,
                "Rice Egg Bowl",
                "Cook rice and eggs and combine them in a bowl.",
                new RecipeIngredient[]{
                        new RecipeIngredient(
                                "rice",
                                200,
                                "g"
                        ),
                        new RecipeIngredient(
                                "egg",
                                2,
                                "pieces"
                        )
                }
        );
    }


    /*
     * Adds one recipe and its ingredients.
     */
    private void addRecipe(
            SQLiteDatabase db, // Receives the database.
            String name, // Receives the recipe name.
            String instructions, // Receives the preparation instructions.
            RecipeIngredient[] ingredients) { // Receives all ingredients.


        // Creates a container for recipe information.
        ContentValues recipeValues = new ContentValues();


        // Stores the recipe name.
        recipeValues.put("name", name);


        // Stores the recipe instructions.
        recipeValues.put("instructions", instructions);


        // Inserts the recipe and receives its generated ID.
        long recipeId =
                db.insert(
                        "recipes",
                        null,
                        recipeValues
                );


        // Loops through every ingredient.
        for (RecipeIngredient ingredient : ingredients) {


            // Creates a container for ingredient information.
            ContentValues ingredientValues =
                    new ContentValues();


            // Stores the recipe ID.
            ingredientValues.put(
                    "recipe_id",
                    recipeId
            );


            // Stores the ingredient name.
            ingredientValues.put(
                    "ingredient_name",
                    ingredient.getName()
            );


            // Stores the required quantity.
            ingredientValues.put(
                    "quantity",
                    ingredient.getQuantity()
            );


            // Stores the required unit.
            ingredientValues.put(
                    "unit",
                    ingredient.getUnit()
            );


            // Inserts the ingredient into the database.
            db.insert(
                    "recipe_ingredients",
                    null,
                    ingredientValues
            );
        }
    }
}