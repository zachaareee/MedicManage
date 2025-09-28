package com.example.medmanage.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.Food;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;
import com.example.medmanage.user_repo.UserRepository;

import java.util.List;


public class UserViewModel extends AndroidViewModel{
    private final UserRepository repository;
    private final LiveData<List<Student>> allStudents;
    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;

    private final LiveData<List<Appointment>> allAppointments;

    public UserViewModel(@NonNull Application application) {
        super(application);
        databaseMedicManage db = databaseMedicManage.getDatabase(application);
        repository = new UserRepository(application);
        allStudents = repository.getAllStudents();
        allNurses = repository.getAllNurses();
        allMedications = repository.getAllMedications();
        allAppointments = repository.getAllAppointments();
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

    public void insertFood(Food food) {
        repository.insertFood(food);
    }

    public void insertAppointment(Appointment appointment) {
        repository.insertAppointment(appointment);
    }

    public void updateMedication(Medication medication) {
        repository.updateMedication(medication);
    }

    public void updateStudent(Student student) {
        repository.updateStudent(student);
    }

    public void updateNurse(Nurse nurse) {
        repository.updateNurse(nurse);
    }

    public void updateFood(Food food) {
        repository.updateFood(food);
    }

    public void deleteMedication(Medication medication) {
        repository.deleteMedication(medication);
    }

    public void deleteStudent(Student student) {
        repository.deleteStudent(student);
    }

    public void deleteNurse(Nurse nurse) {
        repository.deleteNurse(nurse);
    }

    public void deleteFood(Food food) {
        repository.deleteFood(food);
    }

    public void deleteAppointment(Appointment appointment) {
        repository.deleteAppointment(appointment);
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

    public LiveData<List<Appointment>> getAllAppointments() {
        return allAppointments;
    }

    public Medication getMedicationByName(String name) {
        return repository.getMedicationByName(name);
    }

    public Student getStudentByUsername(String username) {
        return repository.getStudentByUsername(username);
    }
}
