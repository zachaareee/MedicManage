
package com.example.medmanage.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class AppointmentWithStudent {
    @Embedded
    public Appointment appointment;

    @Relation(
            parentColumn = "stuNum",
            entityColumn = "stuNum"
    )
    public Student student;
}