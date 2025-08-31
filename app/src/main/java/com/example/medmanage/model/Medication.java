package com.example.medmanage.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medication")
public class Medication {

    @PrimaryKey(autoGenerate = true)
    private int medID;
    private String medName;
    private String brand;
    private String dosage;


    private int quantityOnHand;

    // Constructor updated
    public Medication(String medName, String brand, String dosage, int quantityOnHand) {
        this.medName = medName;
        this.brand = brand;
        this.dosage = dosage;
        this.quantityOnHand = quantityOnHand;
    }

    // --- Getters and Setters ---

    public int getMedID() {
        return medID;
    }

    public void setMedID(int medID) {
        this.medID = medID;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getQuantityOnHand() {
        return quantityOnHand;
    }

    public void setQuantityOnHand(int quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }
}