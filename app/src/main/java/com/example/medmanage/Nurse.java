package com.example.medmanage;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Nurse")
public class Nurse {
    @ColumnInfo(name = "empNum")
    @PrimaryKey(autoGenerate = false)
            int empNum;
    @ColumnInfo(name = "empName")
    String empName;

    @ColumnInfo(name = "empSurname")
    String empSurname;
    @ColumnInfo(name = "empUsername")
    String empUserName;

    public Nurse(int empNum, String empName, String empSurname, String empUserName){
        this.empNum = empNum;
        this.empName = empName;
        this.empSurname = empSurname;
        this.empUserName = empUserName;

    }
    @Ignore
    public Nurse(){

    }
    public String getEmpName(){ return empName;}
    public String getEmpSurname() {return empSurname;}

    public String getEmpUserName() {return empUserName;}

    public int getEmpNum(){ return empNum;}

    public void setEmpNum(int empNum){ this.empNum = empNum;}

    public void setEmpName(String empName) { this.empName = empName;}

    public void setEmpSurname(String empSurname) {this.empSurname = empSurname;}

    public void setEmpUserName(String empSurname) {this.empUserName = empSurname;}



}
