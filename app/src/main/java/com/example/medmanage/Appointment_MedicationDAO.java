package com.example.medmanage;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

public interface Appointment_MedicationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Appointment_Medication appointmentMedication);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Appointment_Medication> entries);

    @Query("select * from `appointment medication` where appointmentNum = appointmentNum")
    List<Appointment_Medication> getMedicationForAppointment(int appointmentNum);
}
