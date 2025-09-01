package com.example.medmanage.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;
import java.util.List;

public class AppointmentWithMedications {

    @Embedded
    public Appointment appointment;

    @Relation(
            parentColumn = "appointmentNum",
            entityColumn = "medID",
            associateBy = @Junction(Appointment_Medication.class)
    )
    public List<Medication> medications;
}