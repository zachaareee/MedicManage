package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medmanage.model.Student;
import java.util.List;

@Dao
public interface StudentDAO {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void addStudent(Student student);

        @Query("SELECT * FROM Student WHERE stuNum = :studentId LIMIT 1")
        Student getStudentById(int studentId);

        @Query("SELECT * FROM Student WHERE userName = :username AND password = :password LIMIT 1")
        Student getStudentByUsernameAndPassword(String username, String password);

        // This is the missing method that needs to be added
        @Query("SELECT * FROM Student WHERE userName = :username LIMIT 1")
        Student getStudentByUsername(String username);

        @Query("SELECT * FROM Student ORDER BY stuSurname ASC")
        LiveData<List<Student>> getAllStudents();

        @Query("DELETE FROM Student")
        void deleteAll();

        @Update
        void updateStudent(Student student);
}