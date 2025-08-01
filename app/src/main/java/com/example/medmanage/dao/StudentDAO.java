package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.medmanage.model.Student;

import java.util.List;
@Dao
public interface StudentDAO {
        @Insert
        void addStudent(Student student);
        @Update
        void updateStudent(Student student);
        @Delete
        void deleteStudent(Student student);
        @Query("select * from student")
        LiveData<List<Student>> getAllStudents();

        @Query("select * from student where userName =:empUsername AND password =:password LIMIT  1")
        Student getNStudentByUsernameAndPassword(String empUsername, String password);
}
