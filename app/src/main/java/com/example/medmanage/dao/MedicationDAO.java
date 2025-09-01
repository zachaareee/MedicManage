package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.medmanage.model.Medication;
import java.util.List;

@Dao
public interface MedicationDAO {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(Medication medication);

        @Update
        void updateMedication(Medication medication);

        @Delete
        void deleteMedication(Medication medication);

        @Query("SELECT * FROM medication ORDER BY medName ASC")
        LiveData<List<Medication>> getAllMedications();

        @Query("SELECT * FROM Medication WHERE medID IN (:medIds)")
        List<Medication> getMedicationsByIds(List<Integer> medIds);

        // This is the missing method needed by ScheduleAppointmentActivity
        @Query("SELECT * FROM Medication WHERE medName LIKE :name LIMIT 1")
        Medication getMedicationByName(String name);

        @Query("SELECT DISTINCT medName FROM medication ORDER BY medName ASC")
        LiveData<List<String>> getDistinctMedNames();

        @Query("SELECT DISTINCT brand FROM medication WHERE medName = :medName ORDER BY brand ASC")
        LiveData<List<String>> getBrandsForMedName(String medName);

        @Query("SELECT DISTINCT dosage FROM medication WHERE medName = :medName AND brand = :brand ORDER BY dosage ASC")
        LiveData<List<String>> getDosagesForMedNameAndBrand(String medName, String brand);

        @Query("SELECT * FROM medication WHERE medName = :medName AND brand = :brand AND dosage = :dosage LIMIT 1")
        LiveData<Medication> getMedicationDetails(String medName, String brand, String dosage);
}