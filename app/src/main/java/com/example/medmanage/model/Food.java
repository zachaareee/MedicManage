package com.example.medmanage.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Food")
public class Food  implements Serializable {
    @ColumnInfo(name = "foodID")
    @PrimaryKey(autoGenerate = true)
    int foodID;
    @ColumnInfo(name = "Brand")
    String foodBrand;

    @ColumnInfo(name = "Name")
    String foodName;


    @ColumnInfo(name = "qty")
    int quantity;

    public Food(int foodID, String foodBrand, String foodName, int quantity){

        this.foodID = foodID;
        this.foodBrand = foodBrand;
        this.foodName = foodName;
        this.quantity = quantity;

    }

@Ignore
    public Food()
    {

    }
    public int getFoodID()
    {
        return foodID;
    }
    public String getFoodBrand()
    {
        return foodBrand;
    }
    public String getFoodName()
    {
        return foodName;
    }
    public int getQuantity(){
        return quantity;
    }
    public void setFoodID(int id)
    {
        foodID = id;
    }
    public void setFoodBrand(String brand){
        foodBrand = brand;
    }
    public void setQuantity(int quantity){
        this.quantity =quantity;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name:  " + foodName + " ," + "Quantity:"+ " " + quantity;
     }
}
