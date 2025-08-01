package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medmanage.model.Medication;

import java.util.List;
@Dao
public interface MedicationDAO {

        @Insert
        void addMedication(Medication medication);
        @Update
        void updateMedication(Medication medication);
        @Delete
        void deleteMedication(Medication medication);
        @Query("select * from medication")
        LiveData<List<Medication>> getAllMedication();

        @Query("select * from medication where medID== :medID")
        Medication getMedName(int medID);


}
