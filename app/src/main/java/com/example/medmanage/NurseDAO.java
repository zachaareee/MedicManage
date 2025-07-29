package com.example.medmanage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
interface NurseDAO {
    @Insert
    void addNurse(Nurse nurse);
    @Update
    void updateNurse(Nurse nurse);
    @Delete
    void deleteNurse(Nurse nurse);
    @Query("select * from nurse")
    LiveData<List<Nurse>> getAllNurses();

    @Query("select * from nurse where empNum== :empNum")
    Nurse getNurse(int empNum);

}
