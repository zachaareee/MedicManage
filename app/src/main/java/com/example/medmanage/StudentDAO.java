package com.example.medmanage;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
interface StudentDAO {
        @Insert
        void addStudent(Student student);
        @Update
        void updateStudent(Student student);
        @Delete
        void deleteStudent(Student student);
        @Query("select * from student")
        LiveData<List<Student>> getAllStudents();

        @Query("select * from student where stuNum==:stuNum")
        Student getStudent(int stuNum);
}
