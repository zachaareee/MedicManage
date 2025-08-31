package com.example.medmanage.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "Appointment",
        foreignKeys = {
                @ForeignKey(entity = Student.class, parentColumns = "stuNum", childColumns = "stuNum", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Nurse.class, parentColumns = "empNum", childColumns = "empNum", onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Food.class, parentColumns = "foodID", childColumns = "foodID", onDelete = ForeignKey.CASCADE)
        })
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    public int appointmentNum;

    @ColumnInfo(index = true)
    public int stuNum;

    @ColumnInfo(index = true)
    public int empNum;

    @ColumnInfo(index = true)
    public int foodID;

    public String date;
    public String time;

    public Appointment(int stuNum, int empNum, int foodID, String date, String time) {
        this.stuNum = stuNum;
        this.empNum = empNum;
        this.foodID = foodID;
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public int getAppointmentNum() {
        return appointmentNum;
    }

    public void setAppointmentNum(int appointmentNum) {
        this.appointmentNum = appointmentNum;
    }

    public int getStuNum() {
        return stuNum;
    }

    public void setStuNum(int stuNum) {
        this.stuNum = stuNum;
    }

    public int getEmpNum() {
        return empNum;
    }

    public void setEmpNum(int empNum) {
        this.empNum = empNum;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}