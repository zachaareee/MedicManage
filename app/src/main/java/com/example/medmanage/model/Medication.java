package com.example.medmanage.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medication")
public class Medication {

    @PrimaryKey(autoGenerate = true)
    private int medID;
    private String name;
    private String description;
    // The quantity field needs to be accessible to Room.
    private int quantity;
    private int stock_available;

    // Constructor
    public Medication(String name, String description, int quantity, int stock_available) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.stock_available = stock_available;
    }

    // Getters and Setters for Room to access the fields.
    // This is the crucial fix: a public getter for the quantity.
    public int getQuantity() {
        return quantity;
    }

    // Other getters and setters...
    public int getMedID() {
        return medID;
    }

    public void setMedID(int medID) {
        this.medID = medID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStock_available() {
        return stock_available;
    }

    public void setStock_available(int stock_available) {
        this.stock_available = stock_available;
    }
}