package com.example.medmanage.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

import java.util.concurrent.ExecutorService;

public class UpdateProfileActivity extends AppCompatActivity {

    // UI Elements
    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private EditText staffNoEditText, studentNoEditText, medicationReqEditText;
    private TextView staffNoLabel;
    private LinearLayout studentFieldsLayout;
    private RadioGroup foodReqRadioGroup;
    private Button confirmButton, cancelButton;

    // Database and User Data
    private databaseMedicManage db;
    private Object currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);


        databaseMedicManage.getDatabase(getApplicationContext(),null);
        initializeViews();

        currentUser = getIntent().getSerializableExtra("USER_TO_EDIT");

        if (currentUser != null) {
            populateFields();
        }

        setupButtonClickListeners();
    }

    private void initializeViews() {
        firstNameEditText = findViewById(R.id.editText_firstName);
        lastNameEditText = findViewById(R.id.editText_lastName);
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        staffNoEditText = findViewById(R.id.editText_staffNo);
        staffNoLabel = findViewById(R.id.label_staffNo);
        studentFieldsLayout = findViewById(R.id.layout_studentFields);
        studentNoEditText = findViewById(R.id.editText_studentNo);
        medicationReqEditText = findViewById(R.id.editText_medicationReq);
        foodReqRadioGroup = findViewById(R.id.radioGroup_foodReq);
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.button_cancel);
    }

    private void populateFields() {
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            studentFieldsLayout.setVisibility(View.VISIBLE);
            staffNoEditText.setVisibility(View.GONE);
            staffNoLabel.setVisibility(View.GONE);

            firstNameEditText.setText(student.getStuName());
            lastNameEditText.setText(student.getStuSurname());
            studentNoEditText.setText(String.valueOf(student.getStuNum()));
            usernameEditText.setText(student.getUserName());
            passwordEditText.setText(student.getPassword());
            medicationReqEditText.setText(student.getMedReq());
            if ("Yes".equalsIgnoreCase(student.getFoodReq())) {
                ((RadioButton) findViewById(R.id.radioButton_foodYes)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.radioButton_foodNo)).setChecked(true);
            }
        } else if (currentUser instanceof Nurse) {
            Nurse nurse = (Nurse) currentUser;
            studentFieldsLayout.setVisibility(View.GONE);
            staffNoEditText.setVisibility(View.VISIBLE);
            staffNoLabel.setVisibility(View.VISIBLE);

            firstNameEditText.setText(nurse.getEmpName());
            lastNameEditText.setText(nurse.getEmpSurname());
            staffNoEditText.setText(String.valueOf(nurse.getEmpNum()));
            usernameEditText.setText(nurse.getEmpUserName());
            passwordEditText.setText(nurse.getPassword());
        }
    }

    private void setupButtonClickListeners() {
        confirmButton.setOnClickListener(v -> updateUser());
        cancelButton.setOnClickListener(v -> finish());
    }


private void updateUser() {
    // Read all data from UI fields on the main thread FIRST
    final String firstName = firstNameEditText.getText().toString().trim();
    final String lastName = lastNameEditText.getText().toString().trim();
    final String username = usernameEditText.getText().toString().trim();
    final String password = passwordEditText.getText().toString().trim();
    final int selectedFoodReqId = foodReqRadioGroup.getCheckedRadioButtonId();
    final String medication = medicationReqEditText.getText().toString().trim();

    // Now execute the database operation on a background thread
    ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
    executor.execute(() -> {
        if (currentUser instanceof Student) {
            // Pass the collected data to the student update method
            final String foodReq = (selectedFoodReqId == R.id.radioButton_foodYes) ? "Yes" : "No";
            updateStudent(firstName, lastName, username, password, medication, foodReq);

        } else if (currentUser instanceof Nurse) {
            updateNurse(firstName, lastName, username, password);
        }
    });
}

    // Modified updateStudent to accept parameters
    private void updateStudent(String firstName, String lastName, String username, String password, String medication, String foodReq) {
        Student student = (Student) currentUser;
        student.setStuName(firstName);
        student.setStuSurname(lastName);
        student.setUserName(username);
        student.setPassword(password);
        student.setMedReq(medication);
        student.setFoodReq(foodReq);

        db.studentDAO().updateStudent(student);
        runOnUiThread(() -> {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the previous screen
        });
    }

    // Modified updateNurse to accept parameters (do the same for the nurse)
    private void updateNurse(String firstName, String lastName, String username, String password) {
        Nurse nurse = (Nurse) currentUser;
        nurse.setEmpName(firstName);
        nurse.setEmpSurname(lastName);
        nurse.setEmpUserName(username);
        nurse.setPassword(password);

        db.nurseDAO().updateNurse(nurse);
        runOnUiThread(() -> {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
