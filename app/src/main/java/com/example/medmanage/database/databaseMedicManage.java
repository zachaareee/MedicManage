package com.example.medmanage.database;

import android.content.Context;
import  android.os.AsyncTask;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.annotation.NonNull;

import com.example.medmanage.dao.FoodDAO;
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

@Database(entities = {Student.class, Nurse.class, Medication.class, Food.class}, version = 1)
public abstract class databaseMedicManage extends RoomDatabase {
    public abstract StudentDAO studentDAO();

    public abstract NurseDAO nurseDAO();

    public abstract MedicationDAO medicationDAO();
    public abstract FoodDAO foodDAO();

    private static volatile databaseMedicManage INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Corrected getDatabase() method in databaseMedicManage.java
    public static databaseMedicManage getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (databaseMedicManage.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    databaseMedicManage.class, "medicmanage_db")
                            .addCallback(sRoomDatabaseCallback) // Corrected line
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //Populate the db with initial data when it is created
    public static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //Execute AsyncTask to populate data
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    //Perfom db operations in backround
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private final NurseDAO mNurseDao;
        private final StudentDAO mStudentDao;
        private final FoodDAO mFoodDao;

        private final MedicationDAO mMedicationDao;



        PopulateDbAsyncTask(databaseMedicManage db) {
            mNurseDao = db.nurseDAO();
            mStudentDao = db.studentDAO();
            mFoodDao = db.foodDAO();
            mMedicationDao = db.medicationDAO();

        }

        @Override
        protected Void doInBackground(final Void... params) {

            mNurseDao.addNurse(new Nurse(1001, "Sara", "Langa", "nurse1", "password"));
            mNurseDao.addNurse(new Nurse(1002, "John", "Smith", "nurse2", "securepass"));

            mStudentDao.addStudent(new Student(227050010, "Phumela", "Mdatyulwa", "student1", "Yes", "Blood Pressure ", "pas123"));
            mStudentDao.addStudent(new Student(225703262, "Zachary", "Jacobs", "student2", "No", "Diabetes ", "lol29"));

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


            mMedicationDao.addMedication(new Medication("AntiDepressent", ""));

            return null;
        }
    }
}