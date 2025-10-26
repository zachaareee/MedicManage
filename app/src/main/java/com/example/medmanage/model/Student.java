package com.example.medmanage.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Student", indices = {@Index(value = {"userName"}, unique = true)})public class Student implements Serializable {
    @PrimaryKey
    private int stuNum;
    private String stuName;
    private String stuSurname;
    private String userName;
    private String foodReq;
    private String medReq;
    private String password;

    public Student(int stuNum, String stuName, String stuSurname, String userName, String foodReq, String medReq, String password) {
        this.stuNum = stuNum;
        this.stuName = stuName;
        this.stuSurname = stuSurname;
        this.userName = userName;
        this.foodReq = foodReq;
        this.medReq = medReq;
        this.password = password;
    }

    // Standard Getters and Setters with Correct Naming
    public int getStuNum() { return stuNum; }
    public void setStuNum(int stuNum) { this.stuNum = stuNum; }
    public String getStuName() { return stuName; }
    public void setStuName(String stuName) { this.stuName = stuName; }
    public String getStuSurname() { return stuSurname; }
    public void setStuSurname(String stuSurname) { this.stuSurname = stuSurname; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getFoodReq() { return foodReq; }
    public void setFoodReq(String foodReq) { this.foodReq = foodReq; }
    public String getMedReq() { return medReq; } // Corrected Name
    public void setMedReq(String medReq) { this.medReq = medReq; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}