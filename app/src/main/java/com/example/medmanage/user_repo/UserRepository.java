package com.example.medmanage.user_repo;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.medmanage.dao.AppointmentDAO;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.dao.MedicationDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.dao.StudentDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.AppointmentDetails;
import com.example.medmanage.model.Food;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

import java.util.List;

public class UserRepository {

    private final StudentDAO studentDAO;
    private final NurseDAO nurseDAO;
    private final MedicationDAO medicationDAO;
    private final FoodDAO foodDAO;
    private final AppointmentDAO appointmentDAO;

    private final LiveData<List<Student>> allStudents;

    private final LiveData<List<Nurse>> allNurses;
    private final LiveData<List<Medication>> allMedications;
    private final LiveData<List<Appointment>> allAppointments; // Added for consistency

    public UserRepository(Application application) {
        databaseMedicManage db = databaseMedicManage.getDatabase(application);
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

    public void updateMedication(Medication medication) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> medicationDAO.updateMedication(medication));
    }

    public void updateStudent(Student student) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> studentDAO.updateStudent(student));
    }

    public void updateNurse(Nurse nurse) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> nurseDAO.updateNurse(nurse));
    }

    public void updateFood(Food food) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> foodDAO.updateFood(food));
    }

    public void deleteMedication(Medication medication) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> medicationDAO.deleteMedication(medication));
    }

    public void deleteStudent(Student student) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> studentDAO.deleteStudent(student));
    }

    public void deleteNurse(Nurse nurse) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> nurseDAO.deleteNurse(nurse));
    }

    public void deleteFood(Food food) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> foodDAO.deleteFood(food));
    }

    public void deleteAppointment(Appointment appointment) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> appointmentDAO.deleteAppointment(appointment));
    }



    // LiveData getters for observable data
    public LiveData<List<Student>> getAllStudents() { return allStudents; }
    public LiveData<List<Nurse>> getAllNurses() { return allNurses; }
    public LiveData<List<Medication>> getAllMedications() { return allMedications; }
    public LiveData<List<Appointment>> getAllAppointments() { return allAppointments; }

    public Medication getMedicationByName(String name) {
        return medicationDAO.getMedicationByName(name);
    }

    public Student getStudentByUsername(String username) {
        return studentDAO.getStudentByUsername(username);
    }
    public LiveData<AppointmentDetails> getActiveAppointmentDetails(int studentId) {
        return appointmentDAO.getActiveAppointmentDetails(studentId);
    }

    public void cancelAppointment(Appointment appointment) {
        databaseMedicManage.databaseWriteExecutor.execute(() ->
                appointmentDAO.cancelAppointment(appointment));
    }






}