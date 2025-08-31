package com.example.medmanage.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.medmanage.model.Appointment;
import java.util.List;

@Dao
public interface AppointmentDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppointment(Appointment appointment);

    // Added a @Delete method for consistency
    @Delete
    void deleteAppointment(Appointment appointment);

    @Query("SELECT * FROM appointment")
    List<Appointment> getAllAppointments();

    // Corrected the WHERE clause to use the method's parameter
    @Query("SELECT * FROM appointment WHERE appointmentNum = :appointmentNum LIMIT 1")
    Appointment getAppointmentById(int appointmentNum);

    // Added the necessary method to check for double-bookings
    @Query("SELECT * FROM appointment WHERE date = :date AND time = :time LIMIT 1")
    Appointment getAppointmentByDateTime(String date, String time);
}