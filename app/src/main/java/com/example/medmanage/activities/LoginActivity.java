package com.example.medmanage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RadioGroup userTypeGroup;
    private CheckBox rememberMeCheckBox;
    private databaseMedicManage db; // Declare db here
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "MedManagePrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_USER_TYPE = "user_type";
    private static final String PREF_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.sign_in_button);
        userTypeGroup = findViewById(R.id.user_type_group);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        databaseMedicManage.getDatabase(getApplicationContext(), new databaseMedicManage.DatabaseCallback() {
            @Override
            public void onDatabaseReady(databaseMedicManage database) {
                db = database; // Initialize db in the callback
            }
        });
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadPreferences();
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void loadPreferences() {
        boolean remember = sharedPreferences.getBoolean(PREF_REMEMBER, false);
        if (remember) {
            usernameEditText.setText(sharedPreferences.getString(PREF_USERNAME, ""));
            passwordEditText.setText(sharedPreferences.getString(PREF_PASSWORD, ""));
            String userType = sharedPreferences.getString(PREF_USER_TYPE, "student");
            if ("student".equals(userType)) {
                userTypeGroup.check(R.id.student_radio_btn);
            } else {
                userTypeGroup.check(R.id.admin_radio_btn);
            }
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void saveOrClearPreferences(String username, String password, String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMeCheckBox.isChecked()) {
            editor.putString(PREF_USERNAME, username);
            editor.putString(PREF_PASSWORD, password);
            editor.putString(PREF_USER_TYPE, userType);
            editor.putBoolean(PREF_REMEMBER, true);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private void attemptLogin() {

        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final int selectedUserTypeId = userTypeGroup.getCheckedRadioButtonId();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedUserTypeId == -1) {
            Toast.makeText(this, "Please select a user type (Student or Administrator).", Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Object loggedInUser = null;
            String loggedInUserType = "";

            if (selectedUserTypeId == R.id.admin_radio_btn) {
                loggedInUser = db.nurseDAO().getNurseByUsernameAndPassword(username, password);
                loggedInUserType = "nurse";
            } else if (selectedUserTypeId == R.id.student_radio_btn) {
                loggedInUser = db.studentDAO().getStudentByUsernameAndPassword(username, password);
                loggedInUserType = "student";
            }

            final Object finalLoggedInUser = loggedInUser;
            final String finalLoggedInUserType = loggedInUserType;
            handler.post(() -> {
                if (finalLoggedInUser != null) {
                    onLoginSuccess(finalLoggedInUser, username, password, finalLoggedInUserType);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void onLoginSuccess(Object user, String username, String password, String loggedInUserType) {
        String loggedInUsername = "";
        int loggedInStudentId = -1;

        if (user instanceof Nurse) {
            loggedInUsername = ((Nurse) user).getEmpUserName();
        } else if (user instanceof Student) {
            loggedInUsername = ((Student) user).getUserName();
            loggedInStudentId = ((Student) user).getStuNum();
        }

        saveOrClearPreferences(username, password, loggedInUserType);
        Toast.makeText(this, "Login Successful! Welcome, " + loggedInUsername, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USERNAME", loggedInUsername);
        intent.putExtra("USER_TYPE", loggedInUserType);
        intent.putExtra("STUDENT_ID", loggedInStudentId);
        startActivity(intent);
        finish();
    }

}