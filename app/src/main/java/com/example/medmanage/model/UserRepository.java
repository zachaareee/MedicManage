package com.example.medmanage.model;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.medmanage.dao.AppointmentDAO;
import com.example.medmanage.dao.Appointment_MedicationDAO;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.dao.MedicationDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.dao.StudentDAO;
import com.example.medmanage.database.databaseMedicManage;

import java.util.List;

public class UserRepository {

    private final StudentDAO studentDAO;
    private final NurseDAO nurseDAO;
    private final MedicationDAO medicationDAO;
    private final FoodDAO foodDAO;
    private final AppointmentDAO appointmentDAO;
    private final Appointment_MedicationDAO appointmentMedicationDAO;

    private final LiveData<List<Student>> allStudents;
    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;

    public UserRepository(Application application) {
        databaseMedicManage db = databaseMedicManage.getDatabase(application);
        studentDAO = db.studentDAO();
        nurseDAO = db.nurseDAO();
        medicationDAO = db.medicationDAO();
        foodDAO = db.foodDAO();
        appointmentDAO = db.appointmentDAO();
        appointmentMedicationDAO = db.appointmentMedicationDAO();

        allStudents = studentDAO.getAllStudents();
        allNurses = nurseDAO.getAllNurses();
        allMedications = medicationDAO.getAllMedication();
    }

    // Student methods
    public void InsertStudent(Student student) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            studentDAO.addStudent(student);
        });
    }

    // Nurse methods
    public void InsertNurse(Nurse nurse) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            nurseDAO.addNurse(nurse);
        });
    }

    // Medication methods - Fixed method call from addMedication to insert
    public void InsertMedication(Medication medication) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            medicationDAO.insert(medication);
        });
    }

    // Food methods
    public void InsertFood(Food food) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            foodDAO.addFood(food);
        });
    }

    // Appointment methods
    public void InsertAppointment(Appointment appointment) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            appointmentDAO.insertAppointment(appointment);
        });
    }

    // Appointment_Medication methods
    public void InsertAppointmentMedication(Appointment_Medication appointmentMedication) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            appointmentMedicationDAO.insert(appointmentMedication);
        });
    }

    // LiveData getters for observable data
    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }

    public LiveData<List<Nurse>> getAllNurses() {
        return allNurses;
    }

    public LiveData<List<Medication>> getAllMedications() {
        return allMedications;
    }

    // Non-LiveData getters for Food, Appointment, and Appointment_Medication
    //These return List directly. If we need LiveData, update the DAOs to return LiveData.
    public List<Food> getAllFood() {
        return foodDAO.getAllFood();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentDAO.getAllAppointments();
    }

    public List<Appointment_Medication> getMedicationForAppointment(int appointmentNum) {
        return appointmentMedicationDAO.getMedicationForAppointment(appointmentNum);
    }
}