package com.example.medmanage.activities;

import android.os.Bundle;
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

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private EditText studentNoEditText, medicationReqEditText;
    private LinearLayout studentFieldsLayout;
    private RadioGroup foodReqRadioGroup;
    private Button confirmButton, cancelButton;

    private databaseMedicManage db;
    private Object currentUser;
    private String username;
    private String userType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update);

        db = databaseMedicManage.getDatabase(getApplicationContext());

        firstNameEditText = findViewById(R.id.editText_firstName);
        lastNameEditText = findViewById(R.id.editText_lastName);
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        studentFieldsLayout = findViewById(R.id.layout_studentFields);
        studentNoEditText = findViewById(R.id.editText_studentNo);
        medicationReqEditText = findViewById(R.id.editText_medicationReq);
        foodReqRadioGroup = findViewById(R.id.radioGroup_foodReq);
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.button_cancel);

        username = getIntent().getStringExtra("USERNAME");
        userType = getIntent().getStringExtra("USER_TYPE");

        fetchUser();

        confirmButton.setOnClickListener(v -> updateUser());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void fetchUser() {
        if ("student".equalsIgnoreCase(userType)) {
            db.studentDAO().getStudentByUsernameLive(username)
                    .observe(this, student -> {
                        if (student != null) {
                            currentUser = student;
                            populateFieldsStudent(student);
                        }
                    });
        } else if ("nurse".equalsIgnoreCase(userType)) {
            db.nurseDAO().getNurseByUsernameLive(username)
                    .observe(this, nurse -> {
                        if (nurse != null) {
                            currentUser = nurse;
                            populateFieldsNurse(nurse);
                        }
                    });
        }
    }

    private void populateFieldsStudent(Student student) {
        studentFieldsLayout.setVisibility(LinearLayout.VISIBLE);
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
    }

    private void populateFieldsNurse(Nurse nurse) {
        studentFieldsLayout.setVisibility(LinearLayout.GONE);
        firstNameEditText.setText(nurse.getEmpName());
        lastNameEditText.setText(nurse.getEmpSurname());
        usernameEditText.setText(nurse.getEmpUserName());
        passwordEditText.setText(nurse.getPassword());
    }

    private void updateUser() {
        final String firstName = firstNameEditText.getText().toString().trim();
        final String lastName = lastNameEditText.getText().toString().trim();
        final String usernameInput = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            if (currentUser instanceof Student) {
                final String medication = medicationReqEditText.getText().toString().trim();
                final String foodReq = ((RadioButton) findViewById(foodReqRadioGroup.getCheckedRadioButtonId()))
                        .getText().toString();

                Student student = (Student) currentUser;
                student.setStuName(firstName);
                student.setStuSurname(lastName);
                student.setUserName(usernameInput);
                student.setPassword(password);
                student.setMedReq(medication);
                student.setFoodReq(foodReq);

                db.studentDAO().updateStudent(student);

            } else if (currentUser instanceof Nurse) {
                Nurse nurse = (Nurse) currentUser;
                nurse.setEmpName(firstName);
                nurse.setEmpSurname(lastName);
                nurse.setEmpUserName(usernameInput);
                nurse.setPassword(password);

                db.nurseDAO().updateNurse(nurse);
            }

            // Notify parent activity to refresh using runOnUiThread
            runOnUiThread(() -> {
                Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // this signals ViewProfileDetailsActivity to refresh
                finish();
            });
        });
    }
}
