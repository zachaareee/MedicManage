package com.example.medmanage.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medmanage.R;
import com.example.medmanage.dao.StudentDAO;
import com.example.medmanage.dao.NurseDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Student;
import com.example.medmanage.model.Nurse;

public class SigninActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ImageView passwordToggle;
    private CheckBox rememberMeCheckBox;
    private TextView forgotPasswordText;
    private TextView cancelText;

    private boolean isPasswordVisible = false;
    private databaseMedicManage db;
    private StudentDAO studentDAO;
    private NurseDAO nurseDAO;

    private SharedPreferences sharedPreferences; // for remember me
    private static final String PREFS_NAME = "MedManagePrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_sign_in);

        initializeViews();
        db = databaseMedicManage.getDatabase(getApplicationContext());
        studentDAO = db.studentDAO();
        nurseDAO = db.nurseDAO();
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setupListeners();
        loadCredentials();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.sign_in_button);
        passwordToggle = findViewById(R.id.password_toggle);
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);
        forgotPasswordText = findViewById(R.id.forgot_password_text);
        cancelText = findViewById(R.id.cancel_text);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        forgotPasswordText.setOnClickListener(v -> showForgotPasswordDialog());
        cancelText.setOnClickListener(v -> finish());
    }

    private void loadCredentials() {
        boolean shouldRemember = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (shouldRemember) {
            usernameEditText.setText(sharedPreferences.getString(KEY_USERNAME, ""));
            passwordEditText.setText(sharedPreferences.getString(KEY_PASSWORD, ""));
            rememberMeCheckBox.setChecked(true);
        }
    }

    private void saveCredentials() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMeCheckBox.isChecked()) {
            editor.putString(KEY_USERNAME, usernameEditText.getText().toString());
            editor.putString(KEY_PASSWORD, passwordEditText.getText().toString());
            editor.putBoolean(KEY_REMEMBER_ME, true);
        } else {
            editor.clear();
        }
        editor.apply();
    }

    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.forgot_password_dialog, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        final EditText usernameResetEditText = dialogView.findViewById(R.id.username_reset_edit_text);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        positiveButton.setOnClickListener(v -> {
            String username = usernameResetEditText.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(SigninActivity.this, "Please enter your username", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SigninActivity.this, "Password reset link sent to the associated email.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordToggle.setImageResource(R.drawable.icon_password_visible);
            passwordEditText.setTypeface(Typeface.DEFAULT);
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordToggle.setImageResource(R.drawable.icon_password_hidden);
            passwordEditText.setTypeface(Typeface.MONOSPACE);
        }
        passwordEditText.setSelection(passwordEditText.length());
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            Student student = studentDAO.getStudentByUsername(username);
            if (student != null && student.getPassword().equals(password)) {
                saveCredentials();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SigninActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", student.getUserName());
                    intent.putExtra("USER_TYPE", "student");
                    intent.putExtra("STUDENT_ID", student.getStuNum());
                    startActivity(intent);
                    finish();
                });
                return;
            }

            Nurse nurse = nurseDAO.getNurseByUsername(username);
            if (nurse != null && nurse.getPassword().equals(password)) {
                saveCredentials();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SigninActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", nurse.getEmpUserName());
                    intent.putExtra("USER_TYPE", "nurse");
                    intent.putExtra("NURSE_ID", nurse.getEmpName());
                    startActivity(intent);
                    finish();
                });
                return;
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            });
        });
    }
}

