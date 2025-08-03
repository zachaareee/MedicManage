package com.example.medmanage.database;

import android.content.Context;
import  android.os.AsyncTask;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.annotation.NonNull;

import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;
import com.example.medmanage.dao.MedicationDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.dao.StudentDAO;

import androidx.sqlite.db.SupportSQLiteDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class, Nurse.class, Medication.class}, version = 1)
public abstract class databaseMedicManage extends RoomDatabase {
    public abstract StudentDAO studentDAO();

    public abstract NurseDAO nurseDAO();

    public abstract MedicationDAO medicationDAO();

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

        PopulateDbAsyncTask(databaseMedicManage db) {
            mNurseDao = db.nurseDAO();
            mStudentDao = db.studentDAO();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mNurseDao.addNurse(new Nurse(1001, "Sara", "Langa", "nurse1", "password"));
            mNurseDao.addNurse(new Nurse(1002, "John", "Smith", "nurse2", "securepass"));

            mStudentDao.addStudent(new Student(227050010, "Phumela", "Mdatyulwa", "student1", "Yes", "Blood Pressure ", "pas123"));
            mStudentDao.addStudent(new Student(225703262, "Zachary", "Jacobs", "student2", "No", "Diabetes ", "lol29"));
          return null;
        }
    }
}