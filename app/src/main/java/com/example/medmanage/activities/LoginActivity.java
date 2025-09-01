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

/**
 * LoginActivity handles user authentication.
 * It uses the UI components from sign_in.xml to log in a user as
 * either a Student or an Administrator (Nurse) and can save credentials
 * if "Remember me" is checked.
 */
public class LoginActivity extends AppCompatActivity {

    // --- UI Components ---
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private RadioGroup userTypeGroup;
    private CheckBox rememberMeCheckBox;

    // --- Database and SharedPreferences ---
    private databaseMedicManage db;
    private SharedPreferences sharedPreferences;

    // Define constants for SharedPreferences
    private static final String PREFS_NAME = "MedManagePrefs";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_USER_TYPE = "user_type";
    private static final String PREF_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        // Initialize UI components using the correct IDs from sign_in.xml
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.sign_in_button);
        userTypeGroup = findViewById(R.id.user_type_group);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        // Initialize Room Database
        db = databaseMedicManage.getDatabase(getApplicationContext());

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Load saved preferences
        loadPreferences();

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    /**
     * Loads credentials from SharedPreferences if "Remember me" was previously checked.
     */
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

    /**
     * Saves or clears credentials in SharedPreferences based on the "Remember me" checkbox.
     */
    private void saveOrClearPreferences(String username, String password, String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMeCheckBox.isChecked()) {
            editor.putString(PREF_USERNAME, username);
            editor.putString(PREF_PASSWORD, password);
            editor.putString(PREF_USER_TYPE, userType);
            editor.putBoolean(PREF_REMEMBER, true);
        } else {
            editor.clear(); // Clear all saved preferences
        }
        editor.apply();
    }


    /**
     * Attempts to log in the user based on the selected user type.
     * Uses a modern background thread instead of the deprecated AsyncTask.
     */
    private void attemptLogin() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final int selectedUserTypeId = userTypeGroup.getCheckedRadioButtonId();

        // --- Input Validation ---
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedUserTypeId == -1) {
            Toast.makeText(this, "Please select a user type (Student or Administrator).", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Database query on a background thread ---
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // Background work
            Object loggedInUser = null;

            if (selectedUserTypeId == R.id.admin_radio_btn) {
                // Try to log in as an Administrator (Nurse)
                loggedInUser = db.nurseDAO().getNurseByUsernameAndPassword(username, password);
            } else if (selectedUserTypeId == R.id.student_radio_btn) {
                // Try to log in as a Student
                loggedInUser = db.studentDAO().getStudentByUsernameAndPassword(username, password);
            }

            final Object finalLoggedInUser = loggedInUser;
            handler.post(() -> {
                // UI work (onPostExecute)
                if (finalLoggedInUser != null) {
                    onLoginSuccess(finalLoggedInUser, username, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Handles the logic for a successful login.
     *
     * @param user The logged-in Nurse or Student object.
     * @param username The username used to log in.
     * @param password The password used to log in.
     */
    private void onLoginSuccess(Object user, String username, String password) {
        String loggedInUsername = "";
        String loggedInUserType = "";

        if (user instanceof Nurse) {
            loggedInUsername = ((Nurse) user).getEmpUserName();
            loggedInUserType = "nurse"; // This corresponds to "Administrator"
        } else if (user instanceof Student) {
            loggedInUsername = ((Student) user).getUserName();
            loggedInUserType = "student";
        }

        // Save preferences if "Remember me" is checked, or clear them if not.
        saveOrClearPreferences(username, password, loggedInUserType);

        // Show success message and navigate to the dashboard
        Toast.makeText(this, "Login Successful! Welcome, " + loggedInUsername, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra("USERNAME", loggedInUsername);
        intent.putExtra("USER_TYPE", loggedInUserType);
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}