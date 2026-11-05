package com.example.smartpantrymanager; // Tells java which pakage this class belongs too

public class PantryItem {
    //Variables needed for the pantry item class
    private int id; //Unique id of item
    private String name; //Ingretient name
    private double quantity;  // Amount of ingrediants
    private String unit; // Measurement of unit
    private String expiryDate; // Has the expiry date of the ingrediant

    public PantryItem ( //Constructor creates the complete Pantry item object
            int id,String name, double quantity,
            String unit, String expiryDate )
    {
        //Saves the information to the variables
        this.id=id;
        this.name=name;
        this.quantity=quantity;
        this.unit=unit;
        this.expiryDate=expiryDate;

    }

    public int getId() {

        return id; //Returns id back to the method
    }

    public String getName() {

        return name;// Sends ingretiandt name back
    }

    public double getQuantity() {

        return quantity; //Sends quantity back
    }

    public String getUnit(){

        return unit; //Send unit back
    }

    public String getExpiryDate() {

        return expiryDate; // Sends the expiry date back
    }


}
