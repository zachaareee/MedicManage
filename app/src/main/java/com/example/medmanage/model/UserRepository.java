package com.example.medmanage.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.medmanage.dao.MedicationDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.dao.StudentDAO;
import com.example.medmanage.database.databaseMedicManage;

import java.util.List;

public class UserRepository {

    private final StudentDAO studentDAO;
    private final NurseDAO nurseDAO;
    private final MedicationDAO medicationDAO;
    private final LiveData<List<Student>> allStudents;
    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;

    public UserRepository(Application application){

        databaseMedicManage db = databaseMedicManage.getDatabase(application);
        studentDAO = db.studentDAO();
        nurseDAO = db.nurseDAO();
        medicationDAO = db.medicationDAO();

        allStudents  = studentDAO.getAllStudents();
        allNurses = nurseDAO.getAllNurses();
        allMedications =  medicationDAO.getAllMedication();





    }
    //Student
    public void InsertStudent(Student student){
        databaseMedicManage.databaseWriteExecutor.execute(() ->{
            studentDAO.addStudent(student);
        });

    }
    //Nurse
    public void InsertNurse(Nurse nurse){
        databaseMedicManage.databaseWriteExecutor.execute(() ->{
            nurseDAO.addNurse(nurse);
        });

    }

    //Medication
    public void InsertMedication(Medication medication){
        databaseMedicManage.databaseWriteExecutor.execute(() ->{
            medicationDAO.addMedication(medication);
        });

    }
    public LiveData<List<Student>> getAllStudents(){
        return allStudents;
    }
    public LiveData<List<Nurse>> getAllNurses(){
        return allNurses;
    }
    public LiveData<List<Medication>> getAllMedications(){
        return allMedications;
    }

}
