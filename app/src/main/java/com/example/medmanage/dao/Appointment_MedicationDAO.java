package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.AppointmentDetails;
import com.example.medmanage.model.AppointmentWithStudent;
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


    @Query("DELETE FROM appointment_medication WHERE appointmentNum = :appointmentNum")
    void deleteLinksForAppointment(int appointmentNum);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppointment(Appointment appointment);
    @Delete
    void deleteAppointment(Appointment appointment);
    @Query("SELECT * FROM appointment ORDER BY date,time ASC")
    LiveData<List<Appointment>>getAllAppointments();
    @Query("SELECT * FROM appointment WHERE date = :date")
    LiveData<List<Appointment>>getAppointmentsByDate(String date);
    @Query("SELECT * FROM appointment WHERE stuNum = :studentId ORDER BY date DESC,time DESC LIMIT 1")
    Appointment getActiveAppointmentForStudent(int studentId);

    @Query("SELECT * FROM appointment WHERE date = :date AND time = :time LIMIT 1")
    Appointment getAppointmentByDateTime(String date, String time);

    @Query("SELECT * FROM Appointment WHERE appointmentNum = :appointmentId LIMIT 1")
    Appointment getAppointmentById(int appointmentId);

    @Transaction
    @Query("SELECT * FROM Appointment ORDER BY date, time")
    LiveData<List<AppointmentWithStudent>> getAllAppointmentsWithStudents();

    // Add these new methods
    @Transaction
    @Query("SELECT * FROM Appointment WHERE appointmentNum = :appointmentId")
    LiveData<AppointmentDetails> getFullAppointmentDetails(int appointmentId);

    @Transaction
    @Query("SELECT * FROM Appointment")
    LiveData<List<AppointmentDetails>> getAllFullAppointmentDetails();


}