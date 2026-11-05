package com.example.smartpantrymanager;

public class Recipe {

    private int id; //Stores recipe id in database
    private String name; // Stores recipe name
    private String instructions; // Stores the instructions for the recipe

    //Constructor to create Recipe object
    public Recipe (

            int id,
            String name,
            String instructions
    )

    //Stores values
    {
       this.id=id;
       this.name=name;
       this.instructions = instructions;
    }

    public int getId() {

        return id; //Returns the recipe id
    }

    public String getName() {

        return name; // Returns the recipe name
    }

    public String getInstructions(){

        return instructions;// Returns instructions
    }

}
