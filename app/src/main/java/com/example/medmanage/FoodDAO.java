package com.example.medmanage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
 interface FoodDAO {

        @Insert
        void addFood(Food food);
        @Update
        void updateFood(Food food);
        @Delete
        void deleteFood(Food food);
        @Query("select * from food")
        List<Food> getAllFood();

        @Query("select * from food where foodID = foodID")
        Food getFoodName(int foodID);

    }


