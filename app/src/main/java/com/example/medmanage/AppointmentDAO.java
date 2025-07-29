package com.example.medmanage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppointmentDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppointment(Appointment appointment);

    @Query("select * from appointment")
    List<Appointment> getAllAppointments();

    @Query("select * from appointment where `Appointment Num`= `Appointment Num`")
    Appointment getAppointmentName(int appointmentNum);





}
