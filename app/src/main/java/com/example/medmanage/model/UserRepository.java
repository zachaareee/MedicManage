package com.example.medmanage.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.medmanage.dao.AppointmentDAO;
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

    private final LiveData<List<Student>> allStudents;
    databaseMedicManage db;
    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;
    private final LiveData<List<Appointment>> allAppointments; // Added for consistency

    public UserRepository(Application application) {
        databaseMedicManage.getDatabase(application, null);
        studentDAO = db.studentDAO();
        nurseDAO = db.nurseDAO();
        medicationDAO = db.medicationDAO();
        foodDAO = db.foodDAO();
        appointmentDAO = db.appointmentDAO();

        allStudents = studentDAO.getAllStudents();
        allNurses = nurseDAO.getAllNurses();
        allMedications = medicationDAO.getAllMedications();
        allAppointments = appointmentDAO.getAllAppointments(); // Correctly get LiveData
    }

    // Insert methods for all entities
    public void insertStudent(Student student) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> studentDAO.addStudent(student));
    }

    public void insertNurse(Nurse nurse) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> nurseDAO.addNurse(nurse));
    }

    public void insertMedication(Medication medication) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> medicationDAO.insert(medication));
    }

    public void insertFood(Food food) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> foodDAO.addFood(food));
    }

    public void insertAppointment(Appointment appointment) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> appointmentDAO.insertAppointment(appointment));
    }

    // LiveData getters for observable data
    public LiveData<List<Student>> getAllStudents() { return allStudents; }
    public LiveData<List<Nurse>> getAllNurses() { return allNurses; }
    public LiveData<List<Medication>> getAllMedications() { return allMedications; }
    public LiveData<List<Appointment>> getAllAppointments() { return allAppointments; }
}