package com.example.medmanage.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.medmanage.database.databaseMedicManage;

import java.util.List;


public class UserViewModel extends AndroidViewModel{
    private final UserRepository repository;
    private final LiveData<List<Student>> allStudents;
    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;

    public UserViewModel(@NonNull Application application) {
        super(application);
        databaseMedicManage db = databaseMedicManage.getDatabase(application,null);
        repository = new UserRepository(application);
        allStudents = repository.getAllStudents();
        allNurses = repository.getAllNurses();
        allMedications = repository.getAllMedications();
    }
    public void insertStudent(Student student){
        repository.insertStudent(student);

    }
    public void insertNurse(Nurse nurse){
        repository.insertNurse(nurse);

    }
    public void insertMedication(Medication medication){
        repository.insertMedication(medication);

    }
    public LiveData<List<Student>> getAllStudents(){
        return allStudents;
    }
    public LiveData<List<Nurse>> getallNurses(){
        return allNurses;
    }
    public LiveData<List<Medication>> getAllMedications(){
        return allMedications;
    }
}
