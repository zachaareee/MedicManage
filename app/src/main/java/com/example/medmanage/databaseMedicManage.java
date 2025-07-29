package com.example.medmanage;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Student.class, Nurse.class, Medication.class}, version = 1)
public abstract class databaseMedicManage extends RoomDatabase{
    public abstract StudentDAO studentDAO();
    public abstract  NurseDAO nurseDAO();
    public abstract  MedicationDAO medicationDAO();

    private static volatile databaseMedicManage INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static databaseMedicManage getDatabase(final  Context context){
        if(INSTANCE == null){
            synchronized (databaseMedicManage.class){
                if(INSTANCE ==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),databaseMedicManage.class,"medicmanage_db").build();
                }
            }
        }
        return INSTANCE;
    }


}
