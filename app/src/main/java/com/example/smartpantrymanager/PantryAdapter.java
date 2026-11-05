package com.example.smartpantrymanager; // Defines the package for the Adapter.
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;



public class PantryAdapter extends BaseAdapter {


    // Stores the Android context.
    private Context context;


    // Stores the pantry items displayed by the ListView.
    private List<PantryItem> pantryItems;


    /*
     * Constructor used to create the Adapter.
     */
    public PantryAdapter(
            Context context, // Receives the current Activity.
            List<PantryItem> pantryItems) { // Receives the pantry data.


        // Stores the Activity context.
        this.context = context;


        // Stores the pantry list.
        this.pantryItems = pantryItems;
    }


    /*
     * Returns the number of pantry items.
     */
    @Override
    public int getCount() {


        // Returns the number of records.
        return pantryItems.size();
    }


    /*
     * Returns one pantry item.
     */
    @Override
    public Object getItem(int position) {


        // Returns the item at the requested position.
        return pantryItems.get(position);
    }


    /*
     * Returns the ID of a pantry item.
     */
    @Override
    public long getItemId(int position) {


        // Returns the database ID.
        return pantryItems.get(position).getId();
    }


    /*
     * Creates and displays one ListView row.
     */
    @Override
    public View getView(
            int position, // Tells us which item is being displayed.
            View convertView, // Reuses an existing row when possible.
            ViewGroup parent) { // Represents the ListView.


        // Checks whether a previous row can be reused.
        if (convertView == null) {


            // Creates the row from item_pantry.xml.
            convertView =
                    LayoutInflater
                            .from(context)
                            .inflate(
                                    R.layout.item_pantry,
                                    parent,
                                    false
                            );
        }


        // Gets the pantry item at this position.
        PantryItem item =
                pantryItems.get(position);


        // Finds the ingredient name TextView.
        TextView nameText =
                convertView.findViewById(
                        R.id.nameText
                );


        // Finds the quantity TextView.
        TextView quantityText =
                convertView.findViewById(
                        R.id.quantityText
                );


        // Finds the expiry TextView.
        TextView expiryText =
                convertView.findViewById(
                        R.id.expiryText
                );


        // Finds the Edit button.
        Button editButton =
                convertView.findViewById(
                        R.id.editButton
                );


        // Finds the Delete button.
        Button deleteButton =
                convertView.findViewById(
                        R.id.deleteButton
                );


        // Displays the ingredient name.
        nameText.setText(
                item.getName()
        );


        // Creates the quantity display.
        quantityText.setText(
                "Quantity: " +
                        formatQuantity(item.getQuantity()) +
                        " " +
                        item.getUnit()
        );


        // Checks whether an expiry date exists.
        if (item.getExpiryDate() != null &&
                !item.getExpiryDate().isEmpty()) {


            // Displays the expiry date.
            expiryText.setText(
                    "Expires: " +
                            item.getExpiryDate() +
                            (isExpiringSoon(item.getExpiryDate())
                                    ? "  (expiring soon!)" : "")
            );


            // Highlights items that are expiring soon in red.
            expiryText.setTextColor(
                    isExpiringSoon(item.getExpiryDate())
                            ? android.graphics.Color.RED
                            : android.graphics.Color.GRAY
            );


        } else {


            // Displays a message when no expiry exists.
            expiryText.setText(
                    "No expiry date"
            );


            // Uses the normal grey colour.
            expiryText.setTextColor(
                    android.graphics.Color.GRAY
            );
        }


        // Runs when the Edit button is clicked.
        editButton.setOnClickListener(view -> {


            // Creates an Intent to open the Add/Edit Activity.
            Intent intent =
                    new Intent(
                            context,
                            AddEditIngredientActivity.class
                    );


            // Sends the selected item's ID to the next Activity.
            intent.putExtra(
                    "pantry_id",
                    item.getId()
            );


            // Opens the Edit screen.
            context.startActivity(intent);
        });


        // Runs when the Delete button is clicked.
        deleteButton.setOnClickListener(view -> {


            // Creates a database helper.
            DatabaseHelper databaseHelper =
                    new DatabaseHelper(context);


            // Deletes the selected database record.
            databaseHelper.deletePantryItem(
                    item.getId()
            );


            // Removes the item from the Adapter list.
            pantryItems.remove(item);


            // Tells ListView that the data changed.
            notifyDataSetChanged();
        });


        // Returns the completed row.
        return convertView;
    }

    /*
     * Shows 2 instead of 2.0 for whole numbers.
     */
    private String formatQuantity(double quantity) {

        // Checks whether the number is a whole number.
        if (quantity == Math.floor(quantity)) {
            return String.valueOf((long) quantity);
        }

        // Returns the decimal number as normal text.
        return String.valueOf(quantity);
    }


    /*
     * Returns true when the expiry date is within 3 days (or already passed),
     * but only when the user enabled expiry alerts in Settings.
     */
    private boolean isExpiringSoon(String expiryDate) {

        // Reads the alert setting saved by SettingsActivity.
        boolean alertsEnabled =
                context.getSharedPreferences(
                        "smart_pantry_settings",
                        Context.MODE_PRIVATE
                ).getBoolean("expiry_alerts", true);

        // Does nothing when alerts are switched off.
        if (!alertsEnabled) {
            return false;
        }

        try {

            // Parses the date in YYYY-MM-DD format.
            java.text.SimpleDateFormat format =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd",
                            java.util.Locale.US
                    );
            format.setLenient(false);
            java.util.Date expiry = format.parse(expiryDate);

            // Calculates how many days are left.
            long millisLeft =
                    expiry.getTime() - System.currentTimeMillis();
            long daysLeft =
                    millisLeft / (24L * 60L * 60L * 1000L);

            // Expiring within 3 days counts as "soon".
            return daysLeft <= 3;

        } catch (Exception exception) {

            // Invalid dates are never flagged.
            return false;
        }
    }
}
