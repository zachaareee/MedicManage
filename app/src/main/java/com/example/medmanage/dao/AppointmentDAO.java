package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
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

    @Delete
    void deleteAppointment(Appointment appointment);

    // ADDED: The missing method to get all appointments.
    @Query("SELECT * FROM appointment ORDER BY date, time ASC")
    LiveData<List<Appointment>> getAllAppointments();

    @Query("SELECT * FROM appointment WHERE date = :date")
    LiveData<List<Appointment>> getAppointmentsByDate(String date);

    @Query("SELECT * FROM appointment WHERE stuNum = :studentId AND date >= :currentDate LIMIT 1")
    Appointment getActiveAppointmentForStudent(int studentId, String currentDate);

    @Query("SELECT * FROM appointment WHERE date = :date AND time = :time LIMIT 1")
    Appointment getAppointmentByDateTime(String date, String time);
}