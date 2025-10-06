package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

public class ViewProfileDetailsActivity extends AppCompatActivity {

    private TextView profileName, profileSurname, profileNumber, profileUsername,
            medicationRequirement, foodRequirement, profilePassword;
    private Button updateButton, deleteButton;
    private databaseMedicManage db;
    private Object currentUser;

    private String username;
    private String userType;

    // Modern Activity Result API (recommended approach)
    private ActivityResultLauncher<Intent> updateProfileLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        db = databaseMedicManage.getDatabase(getApplicationContext());
        initializeViews();

        // Initialize the Activity Result Launcher
        updateProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Check if username was updated
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_USERNAME")) {
                            username = data.getStringExtra("UPDATED_USERNAME");
                        }
                        // Refresh user info after update
                        fetchUserData(this.username, this.userType);
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Get username and type from intent
        username = getIntent().getStringExtra("USERNAME");
        userType = getIntent().getStringExtra("USER_TYPE");

        updateButton.setOnClickListener(v -> openUpdateActivity());
        deleteButton.setOnClickListener(v -> deleteUser());

        // Load initial data
        if (username != null && userType != null) {
            fetchUserData(username, userType);
        }
    }



    private void fetchUserData(String username, String userType) {
        if ("student".equalsIgnoreCase(userType)) {
            db.studentDAO().getStudentByUsernameLive(username).observe(this, student -> {
                if (student != null) {
                    currentUser = student;
                    populateUI();
                }
            });
        } else if ("nurse".equalsIgnoreCase(userType)) {
            db.nurseDAO().getNurseByUsernameLive(username).observe(this, nurse -> {
                if (nurse != null) {
                    currentUser = nurse;
                    populateUI();
                }
            });
        }
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
            medicationRequirement.setText("Medication Requirement\n" + student.getMedReq());
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

            medicationRequirement.setVisibility(View.GONE);
            foodRequirement.setVisibility(View.GONE);
        }
    }

    private void openUpdateActivity() {
        if (currentUser == null) return;

        Intent intent = new Intent(this, UpdateProfileActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("USER_TYPE", userType);
        updateProfileLauncher.launch(intent);
    }

    private void deleteUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_account, null);
        builder.setView(dialogView);

        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (currentUser instanceof Student) {
                db.studentDAO().deleteStudent((Student) currentUser);
            } else if (currentUser instanceof Nurse) {
                db.nurseDAO().deleteNurse((Nurse) currentUser);
            }

            Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}