package com.example.medmanage.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.medmanage.model.Student;

import java.util.List;

@Dao
public interface StudentDAO {

        /**
         * Inserts a new student.
         * If the username is a duplicate, it will be ignored.
         * @return The new row ID, or -1 if the username already exists.
         */
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        long addStudent(Student student); //

        @Update
        void updateStudent(Student student);
        @Query("SELECT * FROM Student")
        LiveData<List<Student>> getAllStudents();

        @Query("SELECT * FROM Student WHERE userName = :username AND password = :password")
        Student getStudent(String username, String password);

        @Query("SELECT * FROM Student WHERE userName = :username")
        Student getStudentByUsername(String username);

        @Query("SELECT * FROM Student WHERE stuNum = :studentId")
        Student getStudentById(int studentId);
        @Delete
        void deleteStudent(Student student);


}
