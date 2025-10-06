// File: SignupActivity.java

package com.example.medmanage.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

import java.util.concurrent.ExecutorService;

public class SignupActivity extends AppCompatActivity {

    // UI Elements
    private RadioGroup userTypeRadioGroup;
    private RadioButton studentRadioButton, nurseRadioButton;
    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private EditText staffNoEditText, studentNoEditText, medicationReqEditText;
    private LinearLayout studentFieldsLayout;
    private RadioGroup foodReqRadioGroup;
    private Button confirmButton, cancelButton;

    // Database
    private databaseMedicManage db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile);
        initializeViews();


        // This makes sure `db` is not null when you needed.
        db =databaseMedicManage.getDatabase(getApplicationContext());
        setupListeners();
        if(savedInstanceState==null){
            studentRadioButton.setChecked(true);
        }
    }

    private void initializeViews() {
        userTypeRadioGroup = findViewById(R.id.radioGroup_userType);
        studentRadioButton = findViewById(R.id.radioButton_student);
        nurseRadioButton = findViewById(R.id.radioButton_nurse);
        firstNameEditText = findViewById(R.id.editText_firstName);
        lastNameEditText = findViewById(R.id.editText_lastName);
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        staffNoEditText = findViewById(R.id.editText_staffNo);
        studentFieldsLayout = findViewById(R.id.layout_studentFields);
        studentNoEditText = findViewById(R.id.editText_studentNo);
        medicationReqEditText = findViewById(R.id.editText_medicationReq);
        foodReqRadioGroup = findViewById(R.id.radioGroup_foodReq);
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.button_cancel);
    }

    private void setupListeners() {
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateUiForUserType(checkedId);
        });

        confirmButton.setOnClickListener(v -> {
            registerUser();
        });

        cancelButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void updateUiForUserType(int checkedId) {
        if (checkedId == R.id.radioButton_student) {
            studentFieldsLayout.setVisibility(View.VISIBLE);
            staffNoEditText.setVisibility(View.GONE);
        } else if (checkedId == R.id.radioButton_nurse) {
            studentFieldsLayout.setVisibility(View.GONE);
            staffNoEditText.setVisibility(View.VISIBLE);
        }
    }

    private void registerUser() {
        // ... (rest of the method is unchanged)
        int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();

        if (selectedUserTypeId == -1) {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure db is not null before proceeding
        if (db == null) {
            Toast.makeText(this, "Database is not yet ready. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedUserTypeId == R.id.radioButton_student) {
            registerNewStudent();
        } else {
            registerNewNurse();
        }
    }

    private void registerNewStudent() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String studentNoStr = studentNoEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String medicationReq = medicationReqEditText.getText().toString().trim();
        String foodReq = ((RadioButton) findViewById(foodReqRadioGroup.getCheckedRadioButtonId())).getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(studentNoStr) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all student fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentNo;
        try {
            studentNo = Integer.parseInt(studentNoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid student number", Toast.LENGTH_SHORT).show();
            return;
        }

        Student newStudent = new Student(studentNo, firstName, lastName, username, foodReq, medicationReq, password);
        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            db.studentDAO().addStudent(newStudent);
            runOnUiThread(() -> {
                Toast.makeText(this, "Student registered successfully!", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }

    private void registerNewNurse() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String staffNoStr = staffNoEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(staffNoStr) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all nurse fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int staffNo;
        try {
            staffNo = Integer.parseInt(staffNoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid staff number", Toast.LENGTH_SHORT).show();
            return;
        }

        Nurse newNurse = new Nurse(staffNo, firstName, lastName, username, password);
        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            db.nurseDAO().addNurse(newNurse);
            runOnUiThread(() -> {
                Toast.makeText(this, "Nurse registered successfully!", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }
}