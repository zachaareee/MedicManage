package com.example.medmanage.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "Appointment", foreignKeys = {
        @ForeignKey(entity = Student.class, parentColumns = "stuID", childColumns = "stuID", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Nurse.class, parentColumns = "empID,", childColumns = "empID", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = Food.class, parentColumns = "foodID", childColumns = "foodID", onDelete = ForeignKey.CASCADE)})

public class Appointment {
    @ColumnInfo(name = "Appointment Num")
    @PrimaryKey(autoGenerate = true)
    int appointmentNum;

    @ColumnInfo(name = "dateTime")
            String dateTime;

    public Appointment(int appointmentNum, String dateTime){
        this.appointmentNum = appointmentNum;
        this.dateTime = dateTime;

    }

    @Ignore
    public Appointment(){}

    public int getAppointmentNum() {
        return appointmentNum;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setAppointmentNum(int appointmentNum) {
        this.appointmentNum = appointmentNum;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
