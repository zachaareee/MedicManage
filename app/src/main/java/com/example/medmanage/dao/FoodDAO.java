package com.example.medmanage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medmanage.model.Food;

import java.util.List;

@Dao
public interface FoodDAO {

        @Insert
        void addFood(Food food);
        @Update
        void updateFood(Food food);
        @Delete
        void deleteFood(Food food);
        @Query("select * from food")
        List<Food> getAllFood();


        @Query("SELECT * FROM food ORDER BY qty DESC")
        List<Food> getAllSortedByQuantityDesc();

    }


