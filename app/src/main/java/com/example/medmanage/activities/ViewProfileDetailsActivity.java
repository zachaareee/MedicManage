package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;

public class ViewProfileDetailsActivity extends AppCompatActivity {

    // ✅ 1. Updated the view variables to match the new XML
    private TextView profileName, profileSurname, profileNumber, profileUsername,
            medicationRequirement, foodRequirement, profilePassword;
    private Button updateButton, deleteButton;
    private databaseMedicManage db;
    private Object currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);
        db = databaseMedicManage.getDatabase(getApplicationContext());
        initializeViews();

        updateButton.setOnClickListener(v -> openUpdateActivity());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String username = getIntent().getStringExtra("USERNAME");
        String userType = getIntent().getStringExtra("USER_TYPE");
        if (username != null && userType != null) {
            fetchUserData(username, userType);
        }
    }

    private void fetchUserData(String username, String userType) {
        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            if ("student".equalsIgnoreCase(userType)) {
                currentUser = db.studentDAO().getStudentByUsername(username);
            } else if ("nurse".equalsIgnoreCase(userType)) {
                currentUser = db.nurseDAO().getNurseByUsername(username);
            }

            if (currentUser != null) {
                new Handler(Looper.getMainLooper()).post(this::populateUI);
            }
        });
    }

    private void initializeViews() {
        profileName = findViewById(R.id.textView_profileName);
        profileSurname = findViewById(R.id.textView_profileSurname);
        profileNumber = findViewById(R.id.textView_profileNumber);
        profileUsername = findViewById(R.id.textView_profileUsername);
        medicationRequirement = findViewById(R.id.textView_medicationRequirement);
        foodRequirement = findViewById(R.id.textView_foodRequirement);
        profilePassword = findViewById(R.id.textView_profilePassword);
        updateButton = findViewById(R.id.button_update);
        deleteButton = findViewById(R.id.button_delete);
    }

    private void populateUI() {
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            profileName.setText("Name\n" + student.getStuName());
            profileSurname.setText("Surname\n" + student.getStuSurname());
            profileUsername.setText("Username\n" + student.getUserName());
            profileNumber.setText("Student Number\n" + student.getStuNum());
            medicationRequirement.setText("Medication Requirement\n" + student.getMedRequirement());
            foodRequirement.setText("Food Requirement\n" + student.getFoodReq());
            profilePassword.setText("Password\n********");

            profileNumber.setVisibility(View.VISIBLE);
            medicationRequirement.setVisibility(View.VISIBLE);
            foodRequirement.setVisibility(View.VISIBLE);

        } else if (currentUser instanceof Nurse) {
            Nurse nurse = (Nurse) currentUser;
            profileName.setText("Name\n" + nurse.getEmpName());
            profileSurname.setText("Surname\n" + nurse.getEmpSurname());
            profileUsername.setText("Username\n" + nurse.getEmpUserName());
            profileNumber.setText("Staff Number\n" + nurse.getEmpNum());
            profilePassword.setText("Password\n********");

            // Hide fields that are only for students
            profileNumber.setVisibility(View.VISIBLE); // Still show this, but with staff number
            medicationRequirement.setVisibility(View.GONE);
            foodRequirement.setVisibility(View.GONE);
        }
    }

    private void openUpdateActivity() {
        if (currentUser == null) return;
        Intent intent = new Intent(this, UpdateProfileActivity.class);

        // This is the key line: it packages the current user's data...
        intent.putExtra("USER_TO_EDIT", (Serializable) currentUser);
        startActivity(intent);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account?")
                .setPositiveButton("Yes, Delete", (dialog, which) -> deleteUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteUser() {
        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            if (currentUser instanceof Student) {
                db.studentDAO().deleteStudent((Student) currentUser);
            } else if (currentUser instanceof Nurse) {
                db.nurseDAO().deleteNurse((Nurse) currentUser);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        });
    }
}