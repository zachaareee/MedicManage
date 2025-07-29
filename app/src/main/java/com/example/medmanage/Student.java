package com.example.medmanage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Student")
public class Student {
    @ColumnInfo(name = "stuNum")
    @PrimaryKey(autoGenerate = false)
    int stuNum;

    @ColumnInfo(name = "stuName")
    String stuName;

    @ColumnInfo(name = "stuSurname")
    String stuSurname;

    @ColumnInfo(name = "medReq")
    String medRequirement;

    @ColumnInfo(name = "foodReq")
    String foodReq;

    @ColumnInfo(name = "userName")
    String userName;

    public Student(int stuNum, String stuName, String stuSurname, String userName, String foodReq, String medRequirement){
        this.stuNum = stuNum;
        this.stuName = stuName;
        this.stuSurname = stuSurname;
        this.userName= userName;
        this.foodReq = foodReq;
        this.medRequirement = medRequirement;

    }

    @Ignore
    public Student(){

    }

    public int getStuNum() {
        return stuNum;
    }

    public String getStuName() {
        return stuName;
    }

    public String getStuSurname(){
      return stuSurname;
    }

    public String getUserName() {
        return userName;
    }

    public String getMedRequirement(){
        return userName;
    }

    public String getFoodReq(){
        return foodReq;
    }

    public void setFoodReq(String foodReq) {
        this.foodReq = foodReq;
    }

    public void setMedRequirement(String medRequirement) {
        this.medRequirement = medRequirement;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public void setStuNum(int stuNum) {
        this.stuNum = stuNum;
    }

    public void setStuSurname(String stuSurname) {
        this.stuSurname = stuSurname;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
