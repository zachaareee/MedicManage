package com.example.medmanage.dao;

import androidx.room.Dao; // You were missing this import
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.medmanage.model.Appointment_Medication;
import java.util.List;

@Dao //
public interface Appointment_MedicationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment_Medication appointmentMedication);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Appointment_Medication> entries);

    @Delete
    void delete(Appointment_Medication appointmentMedication);


    @Query("SELECT * FROM `appointment medication` WHERE appointmentNum = :appointmentNum")
    List<Appointment_Medication> getMedicationForAppointment(int appointmentNum);
}