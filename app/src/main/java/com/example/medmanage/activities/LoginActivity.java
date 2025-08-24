package com.example.medmanage.activities;


import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;


/**
 * LoginActivity handles user authentication.
 * It provides a single login form and attempts to authenticate the user
 * as either a Nurse or a Student.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private databaseMedicManage db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in);

        // Initialize UI components
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        // Initialize Room Database
        db = databaseMedicManage.getDatabase(getApplicationContext());

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    /**
     * Attempts to log in the user.
     * The application first tries to log in as a Nurse, and if that fails,
     * it tries to log in as a Student.
     */
    @SuppressLint("StaticFieldLeak")
    private void attemptLogin() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        // Basic input validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Perform database query in a background thread using AsyncTask
        new AsyncTask<Void, Void, Object>() { // Object can be Nurse, Student, or null
            @Override
            protected Object doInBackground(Void... voids) {
                // First, try to log in as a Nurse
                Nurse nurse = db.nurseDAO().getNurseByUsernameAndPassword(username, password);
                if (nurse != null) {
                    return nurse;
                }

                // If not a Nurse, try to log in as a Student
                @SuppressLint("StaticFieldLeak") Student student = db.studentDAO().getNStudentByUsernameAndPassword(username, password);
                if (student != null) {
                    return student;
                }

                // No user found
                return null;
            }

            @Override
            protected void onPostExecute(Object loggedInUser) {
                if (loggedInUser != null) {
                    String loggedInUsername = "";
                    String loggedInUserType = "";

                    if (loggedInUser instanceof Nurse) {
                        loggedInUsername = ((Nurse) loggedInUser).getEmpUserName();
                        loggedInUserType = "nurse";
                    } else if (loggedInUser instanceof Student) {
                        loggedInUsername = ((Student) loggedInUser).getUserName();
                        loggedInUserType = "student";
                    }

                    // Login successful
                    Toast.makeText(LoginActivity.this, "Login Successful! Welcome, " + loggedInUsername, Toast.LENGTH_SHORT).show();
                    // Navigate to DashboardActivity
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", loggedInUsername);
                    intent.putExtra("USER_TYPE", loggedInUserType);
                    startActivity(intent);
                    finish(); // Close LoginActivity so user can't go back to it
                } else {
                    // Login failed
                    Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}

