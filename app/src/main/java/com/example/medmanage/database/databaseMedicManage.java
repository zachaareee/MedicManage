package com.example.medmanage.database;

import android.content.Context;
import  android.os.AsyncTask;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.annotation.NonNull;

import com.example.medmanage.dao.AppointmentDAO;
import com.example.medmanage.dao.Appointment_MedicationDAO;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.Appointment_Medication;
import com.example.medmanage.model.Food;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;
import com.example.medmanage.dao.MedicationDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.dao.StudentDAO;

import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class, Nurse.class, Medication.class, Food.class, Appointment.class, Appointment_Medication.class}, version = 2)
public abstract class databaseMedicManage extends RoomDatabase {
    public abstract StudentDAO studentDAO();
    public abstract NurseDAO nurseDAO();
    public abstract MedicationDAO medicationDAO();
    public abstract FoodDAO foodDAO();
    public abstract  AppointmentDAO appointmentDAO();
    public abstract Appointment_MedicationDAO appointmentMedicationDAO();

    private static volatile databaseMedicManage INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static databaseMedicManage getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (databaseMedicManage.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    databaseMedicManage.class, "medicmanage_db")
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration(true)
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
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final NurseDAO mNurseDao;
        private final StudentDAO mStudentDao;
        private final FoodDAO mFoodDao;
        private final MedicationDAO mMedicationDao;
        private final AppointmentDAO mAppointmentDAO;
        private final Appointment_MedicationDAO mAppointmentMedicationDAO;

        PopulateDbAsyncTask(databaseMedicManage db) {
            mNurseDao = db.nurseDAO();
            mStudentDao = db.studentDAO();
            mFoodDao = db.foodDAO();
            mMedicationDao = db.medicationDAO();
            mAppointmentDAO = db.appointmentDAO();
            mAppointmentMedicationDAO = db.appointmentMedicationDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // NURSES
            mNurseDao.addNurse(new Nurse(1001, "Sara", "Langa", "nurse1", "password"));
            mNurseDao.addNurse(new Nurse(1002, "John", "Smith", "nurse2", "securepass"));

            // STUDENTS
            mStudentDao.addStudent(new Student(227050010, "Phumela", "Mdatyulwa", "student1", "Yes", "Blood Pressure ", "pas123"));
            mStudentDao.addStudent(new Student(225703262, "Zachary", "Jacobs", "student2", "No", "Diabetes ", "lol29"));

            // FOOD
            mFoodDao.addFood(new Food(1, "Bokomo", "WeetBix", 150));
            mFoodDao.addFood(new Food(2, "Albany", "White Bread", 80));
            mFoodDao.addFood(new Food(3, "Albany", "Brown Bread", 200));
            mFoodDao.addFood(new Food(4, "Clover", "Full Cream Milk", 300));
            mFoodDao.addFood(new Food(5, "Long Life", "Full Cream Milk", 180));
            mFoodDao.addFood(new Food(6, "Koo", "Baked Beans", 90));
            mFoodDao.addFood(new Food(7, "Sasko", "White Flour", 150));
            mFoodDao.addFood(new Food(8, "Sasko", "Brown Flour", 150));
            mFoodDao.addFood(new Food(9, "Hullets", "Brown Sugar", 150));
            mFoodDao.addFood(new Food(10, "HUllets", "White Sugar", 150));
            mFoodDao.addFood(new Food(11, "Tastic", "Long Grain Rice", 150));
            mFoodDao.addFood(new Food(12, "IMBO", "Samp", 150));
            mFoodDao.addFood(new Food(13, "Lucky Star", "Plichards in Tomato", 150));
            mFoodDao.addFood(new Food(14, "IWISA", "Super Maize Meal", 150));
            mFoodDao.addFood(new Food(15, "Shoprite Rite Brand", "Sunflower Oil", 110));

            // MEDICATIONS
            mMedicationDao.insert(new Medication("Paracetamol", "Panado", "500mg", 100));
            mMedicationDao.insert(new Medication("Ibuprofen", "Advil", "200mg", 50));

            // APPOINTMENTS
            mAppointmentDAO.insertAppointment(new Appointment(227050010, 1001, 6, "2025-09-15", "10:30"));

            return null;
        }
    }
}