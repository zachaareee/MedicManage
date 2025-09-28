package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medmanage.model.Nurse;

import java.util.List;
@Dao
public interface NurseDAO {
    @Insert
    void addNurse(Nurse nurse);
    @Update
    void updateNurse(Nurse nurse);
    @Delete
    void deleteNurse(Nurse nurse);
    @Query("select * from nurse")
    LiveData<List<Nurse>> getAllNurses();

    @Query("select * from nurse where empUsername=:empUsername AND password =:password LIMIT  1")
    Nurse getNurseByUsernameAndPassword(String empUsername, String password);

    @Query("SELECT * FROM nurse WHERE empUsername = :empUsername LIMIT 1")
    Nurse getNurseByUsername(String empUsername);
    @Query("SELECT * FROM nurse WHERE empUserName = :username LIMIT 1")
    LiveData<Nurse> getNurseByUsernameLive(String username);



}
