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
            mNurseDao.addNurse(new Nurse(1001, "Sarah", "Miller", "sarahM", "pass123"));
            mNurseDao.addNurse(new Nurse(1002, "David", "Smith", "dsmith", "secure1"));
            mNurseDao.addNurse(new Nurse(1003, "Linda", "Johnson", "lindaj", "hello123"));
            mNurseDao.addNurse(new Nurse(1004, "James", "Brown", "jbrown", "nurse456"));
            mNurseDao.addNurse(new Nurse(1005, "Emily", "Davis", "edavis", "safe789"));
            mNurseDao.addNurse(new Nurse(1006, "Michael", "Wilson", "mwilson", "qwerty1"));
            mNurseDao.addNurse(new Nurse(1007, "Sophia", "Taylor", "staylor", "alpha22"));
            mNurseDao.addNurse(new Nurse(1008, "Robert", "Anderson", "randerson", "bravo33"));
            mNurseDao.addNurse(new Nurse(1009, "Olivia", "Thomas", "othomas", "charlie44"));
            mNurseDao.addNurse(new Nurse(1010, "Daniel", "Moore", "dmoore", "delta55"));
            // STUDENTS
            mStudentDao.addStudent(new Student(227050010, "Phumela", "Mdatyulwa", "student1", "Yes", "Hypertension", "pas123"));  // Lisinopril, Amlodipine
            mStudentDao.addStudent(new Student(227050011, "Emily", "Mokoena", "student2", "Yes", "Asthma", "emily2025"));      // Salbutamol, Fluticasone
            mStudentDao.addStudent(new Student(227050012, "Michael", "Van der Merwe", "student3", "Yes", "Diabetes", "mikeD50")); // Metformin
            mStudentDao.addStudent(new Student(227050013, "Chloe", "Dlamini", "student4", "No", "Hypertension", "chloeBP"));   // Lisinopril, Amlodipine
            mStudentDao.addStudent(new Student(227050014, "Liam", "Peters", "student5", "Yes", "Hypothyroidism", "meriy@")); // Levothyroxine
            mStudentDao.addStudent(new Student(225703262, "Zachary", "Jacobs", "student6", "No", "Diabetes", "lol29"));        // Metformin, Insulin Glargine
            mStudentDao.addStudent(new Student(225703263, "Priya", "Khan", "student7", "Yes", "Depression", "priyaMind"));     // Sertraline
            mStudentDao.addStudent(new Student(225703264, "David", "Naidoo", "student8", "No", "High Cholesterol", "davidH20")); // Atorvastatin, Simvastatin
            mStudentDao.addStudent(new Student(225703265, "Thabo", "Mabena", "student9", "Yes", "Asthma", "TbhMabena"));     // Salbutamol
            mStudentDao.addStudent(new Student(225703266, "Grace", "Nkosi", "student10", "No", "High Cholesterol", "graceMondlana")); // Simvastatin


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
            mMedicationDao.insert(new Medication(5001,"Metformin", "Glucophage", "500mg", 200));     // Diabetes
            mMedicationDao.insert(new Medication(5002,"Insulin Glargine", "Lantus", "100mg", 80));  // Diabetes
            mMedicationDao.insert(new Medication(5003,"Lisinopril", "Zestril", "10mg", 150));       // Hypertension
            mMedicationDao.insert(new Medication(5004,"Amlodipine", "Norvasc", "5mg", 120));        // Hypertension
            mMedicationDao.insert(new Medication(5005,"Atorvastatin", "Lipitor", "20mg", 100));     // High cholesterol
            mMedicationDao.insert(new Medication(5006,"Simvastatin", "Zocor", "40mg", 90));         // High cholesterol
            mMedicationDao.insert(new Medication(5007,"Salbutamol", "Ventolin", "100mg", 60));     // Asthma
            mMedicationDao.insert(new Medication(5008,"Fluticasone", "Flixotide", "250mg", 70));   // Asthma / COPD
            mMedicationDao.insert(new Medication(5009,"Levothyroxine", "Eltroxin", "50mg", 110));  // Hypothyroidism
            mMedicationDao.insert(new Medication(5010,"Sertraline", "Zoloft", "50mg", 75));         // Depression / Anxiety


            // APPOINTMENTS
            mAppointmentDAO.insertAppointment(new Appointment(227050010, 1001, 1, "2025-8-15", "09:00")); // Phumela
            mAppointmentDAO.insertAppointment(new Appointment(225703262, 1002, 2, "2025-08-06", "10:00")); // Zachary
            mAppointmentDAO.insertAppointment(new Appointment(227050011, 1003, 3, "2025-01-07", "11:00")); // Emily
            mAppointmentDAO.insertAppointment(new Appointment(225703263, 1007, 4, "2025-01-08", "12:00")); // Priya
            mAppointmentDAO.insertAppointment(new Appointment(227050012, 1004, 5, "2025-01-09", "13:05")); // Michael
            mAppointmentDAO.insertAppointment(new Appointment(227050013, 1005, 6, "2025-01-10", "12:30")); // Chloe
            mAppointmentDAO.insertAppointment(new Appointment(227050014, 1002, 7, "2025-01-11", "15:00")); // Liam
            mAppointmentDAO.insertAppointment(new Appointment(225703264, 1006, 8, "2025-01-12", "13:30")); // David
            mAppointmentDAO.insertAppointment(new Appointment(225703270, 1003, 9, "2025-01-13", "11:30")); // Thabo
            mAppointmentDAO.insertAppointment(new Appointment(225703266, 1005, 10, "2025-01-14", "10:00")); // Grace

            return null;
        }
    }
}