package com.example.smartpantrymanager;

public class RecipeIngredient {
    private String name; //Stores ingrediant name
    private double quantity; // Stores the quantity needed
    private String unit; // Stores the measurement unit


    public RecipeIngredient (
            String name,
            double quantity,
            String unit
    )

    //Saves the values
    {
        this.name=name;
        this.quantity=quantity;
        this.unit=unit;
    }

    public String getName(){

        return name; // Returns ingrediant name
    }

    public double getQuantity() {

        return quantity; // Reurns Quantity
    }

    public String getUnit() {
        return unit; //Returns Unit
    }

}