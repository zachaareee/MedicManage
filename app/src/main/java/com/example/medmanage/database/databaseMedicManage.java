package com.example.medmanage.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.medmanage.dao.AppointmentDAO;
import com.example.medmanage.dao.Appointment_MedicationDAO;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.dao.MedicationDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.dao.StudentDAO;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.Appointment_Medication;
import com.example.medmanage.model.Food;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Student.class,
                Nurse.class,
                Medication.class,
                Food.class,
                Appointment.class,
                Appointment_Medication.class
        },
        version = 1
)
public abstract class databaseMedicManage extends RoomDatabase {
    public abstract StudentDAO studentDAO();
    public abstract NurseDAO nurseDAO();
    public abstract MedicationDAO medicationDAO();
    public abstract FoodDAO foodDAO();
    public abstract AppointmentDAO appointmentDAO();
    public abstract Appointment_MedicationDAO appointmentMedicationDAO();

    private static volatile databaseMedicManage INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static databaseMedicManage getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (databaseMedicManage.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    databaseMedicManage.class,
                                    "med_manage_database"
                            )
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                // Populate the database with initial data on creation
                populateDatabase(INSTANCE);
            });
        }
    };

    private static void populateDatabase(databaseMedicManage db) {
        StudentDAO studentDao = db.studentDAO();
        NurseDAO nurseDao = db.nurseDAO();
        FoodDAO foodDao = db.foodDAO();
        MedicationDAO medicationDao = db.medicationDAO();
        AppointmentDAO appointmentDAO = db.appointmentDAO();

        // Populate with initial data
        nurseDao.addNurse(new Nurse(1001, "Sara", "Langa", "nurse1", "password"));
        nurseDao.addNurse(new Nurse(1002, "John", "Smith", "nurse2", "securepass"));
        studentDao.addStudent(new Student(227050010, "Phumela", "Mdatyulwa", "student1", "Yes", "Blood Pressure ", "pas123"));
        studentDao.addStudent(new Student(225703262, "Zachary", "Jacobs", "student2", "No", "Diabetes ", "lol29"));
        foodDao.addFood(new Food(1, "Bokomo", "WeetBix", 150));
        foodDao.addFood(new Food(2, "Albany", "White Bread", 80));
        foodDao.addFood(new Food(3, "Albany", "Brown Bread", 200));
        foodDao.addFood(new Food(4, "Clover", "Full Cream Milk", 300));
        foodDao.addFood(new Food(5, "Long Life", "Full Cream Milk", 180));
        foodDao.addFood(new Food(6, "Koo", "Baked Beans", 90));
        foodDao.addFood(new Food(7, "Sasko", "White Flour", 150));
        foodDao.addFood(new Food(8, "Sasko", "Brown Flour", 150));
        foodDao.addFood(new Food(9, "Hullets", "Brown Sugar", 150));
        foodDao.addFood(new Food(10, "HUllets", "White Sugar", 150));
        foodDao.addFood(new Food(11, "Tastic", "Long Grain Rice", 150));
        foodDao.addFood(new Food(12, "IMBO", "Samp", 150));
        foodDao.addFood(new Food(13, "Lucky Star", "Plichards in Tomato", 150));
        foodDao.addFood(new Food(14, "IWISA", "Super Maize Meal", 150));
        foodDao.addFood(new Food(15, "Shoprite Rite Brand", "Sunflower Oil", 110));
        medicationDao.insert(new Medication("Paracetamol", "Panado", "500mg", 100));
        medicationDao.insert(new Medication("Ibuprofen", "Advil", "200mg", 50));
        appointmentDAO.insertAppointment(new Appointment(227050010, 1001, 6, "2025-09-15", "10:30"));
        appointmentDAO.insertAppointment(new Appointment(225703262, 1002, 12, "2025-09-15", "10:30"));
    }
}
