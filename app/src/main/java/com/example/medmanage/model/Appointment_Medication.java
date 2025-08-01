package com.example.medmanage.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(primaryKeys = {"appointmentNum", "medID"},
        tableName = "Appointment Medication",
        foreignKeys = {@ForeignKey(entity = Appointment.class, parentColumns = "appointmentNum", childColumns = "appointmentNum", onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = Medication.class, parentColumns = "medID", childColumns = "medID", onDelete = ForeignKey.CASCADE)},
        indices = {@Index(value = {"appointmentNum"}),
                @Index(value = {"medID"})
        })
public class Appointment_Medication {
    public int appointmentNum;
    public int medID;


}
