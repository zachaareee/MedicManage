package com.example.medmanage.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medmanage.R;
import com.example.medmanage.dao.StudentDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Student;
import com.example.medmanage.model.Nurse;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private databaseMedicManage db;
    private StudentDAO studentDAO;
    private NurseDAO nurseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_sign_in);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.sign_in_button);

        db = databaseMedicManage.getDatabase(getApplicationContext());
        studentDAO = db.studentDAO();
        nurseDAO = db.nurseDAO();

        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            // First, try to get a student
            Student student = studentDAO.getStudentByUsername(username);

            // If a student is found, check their password
            if (student != null && student.getPassword().equals(password)) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", student.getUserName());
                    intent.putExtra("USER_TYPE", "student");
                    intent.putExtra("STUDENT_ID", student.getStuNum());
                    startActivity(intent);
                    finish();
                });
                return;
            }

            // If no student is found, try to get a nurse
            Nurse nurse = nurseDAO.getNurseByUsername(username);

            // If a nurse is found, check their password
            if (nurse != null && nurse.getPassword().equals(password)) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", nurse.getEmpUserName());
                    intent.putExtra("USER_TYPE", "nurse");
                    intent.putExtra("NURSE_ID", nurse.getEmpName());
                    startActivity(intent);
                    finish();
                });
                return;
            }

            // If neither a student nor a nurse is found
            runOnUiThread(() -> {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
