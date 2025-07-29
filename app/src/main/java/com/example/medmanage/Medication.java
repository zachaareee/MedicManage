package com.example.medmanage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Medication")
public class Medication {
    @ColumnInfo(name = "medID")
        @PrimaryKey(autoGenerate = false)
            int medID;
    @ColumnInfo(name = "quantity")
            int quantity;

    @ColumnInfo(name = "medName")
    String medName;

    @ColumnInfo(name = "brand")
    String brand;

    @ColumnInfo(name = "dosage")
    String dosage;

    public Medication(int medID, int quantity, String medName,String brand,String dosage){
        this.medID = medID;
        this.quantity = quantity;
        this.medName = medName;
        this.brand = brand;
        this.dosage = dosage;
    }
    @Ignore
    public Medication()
    {

    }

    public int getMedID()
    {
        return medID;
    }

    public int getMedQty()
    {
        return quantity;
    }
    public String getMedBrand()
    {
        return brand;
    }
    public String getMedName()
    {
        return medName;
    }
    public void setMedID(int id)
    {
        medID = id;
    }
    public void setMedBrand(String brand){
        this.brand = brand;
    }
    public void setMedQty(int quantity){
        this.quantity = quantity;
    }

    public void setMedName(String medName){
        this.medName = medName;

    }


}
