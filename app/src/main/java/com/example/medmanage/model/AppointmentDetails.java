package com.example.medmanage.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.text.ParseException;
import java.util.List;

public class AppointmentDetails {
    @Embedded
    public Appointment appointment;

    @Relation(
            parentColumn = "stuNum",
            entityColumn = "stuNum"
    )
    public Student student;

    @Relation(
            parentColumn = "appointmentNum",
            entityColumn = "medID",
            associateBy = @Junction(Appointment_Medication.class)
    )
    public List<Medication> medications;
}
