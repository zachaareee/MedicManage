package com.example.medmanage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.medmanage.model.Appointment_Medication;
import java.util.List;

@Dao
public interface Appointment_MedicationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment_Medication appointmentMedication);

    @Delete
    void delete(Appointment_Medication appointmentMedication);

    @Query("SELECT medID FROM appointment_medication WHERE appointmentNum = :appointmentNum")
    List<Integer> getMedicationIdsForAppointment(int appointmentNum);

    // ADD THIS NEW METHOD
    @Query("DELETE FROM appointment_medication WHERE appointmentNum = :appointmentNum")
    void deleteLinksForAppointment(int appointmentNum);
}