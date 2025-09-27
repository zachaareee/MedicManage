package com.example.medmanage.model;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private final UserRepository repository;
    private final LiveData<List<Student>> allStudents;
    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;
    private final LiveData<List<Appointment>> allAppointments;

    public UserViewModel(@NonNull Application application) {
        super(application);
        // The ViewModel has one dependency: the Repository.
        repository = new UserRepository(application);
        allStudents = repository.getAllStudents();
        allNurses = repository.getAllNurses();
        allMedications = repository.getAllMedications();
        allAppointments = repository.getAllAppointments();
    }

    // --- Data Modification Methods ---
    // These methods simply pass the request along to the repository.
    public void insertStudent(Student student) {
        repository.insertStudent(student);
    }
    public void insertNurse(Nurse nurse) {
        repository.insertNurse(nurse);
    }
    public void insertMedication(Medication medication) {
        repository.insertMedication(medication);
    }
    public void insertAppointment(Appointment appointment) {
        repository.insertAppointment(appointment);
    }
    public void deleteAppointment(Appointment appointment) {
        repository.deleteAppointment(appointment);
    }

    // --- Data Retrieval Methods ---
    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }
    public LiveData<List<Nurse>> getAllNurses() {
        return allNurses;
    }
    public LiveData<List<Medication>> getAllMedications() {
        return allMedications;
    }
    public void updateMedication(Medication medication) {
        repository.updateMedication(medication);

    }
    public void deleteMedication(Medication medication) {
        repository.deleteMedication(medication);
    }
    public LiveData<List<Appointment>> getAllAppointments() {
        return allAppointments;
    }

    public LiveData<AppointmentDetails> getActiveAppointmentDetails(int studentId) {
        return repository.getActiveAppointmentDetails(studentId);
    }

    public void cancelAppointment(Appointment appointment) {
        repository.cancelAppointment(appointment);
    }





}