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
import androidx.constraintlayout.widget.Group;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

public class ViewProfileDetailsActivity extends AppCompatActivity {

    private TextView profileName, profileSurname, studentNumber, staffNumber, profileUsername,
            medicationRequirement, foodRequirement, profilePassword;
    private Group studentFieldsGroup, nurseFieldsGroup;
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
        setContentView(R.layout.profile_view);

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
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        // Load initial data
        if (username != null && userType != null) {
            fetchUserData(username, userType);
        }
    }

    private void fetchUserData(String username, String userType) {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            if ("student".equalsIgnoreCase(userType)) {
                Student student = db.studentDAO().getStudentByUsername(username);
                runOnUiThread(() -> {
                    if (student != null) {
                        currentUser = student;
                        populateUI();
                    }
                });
            } else if ("nurse".equalsIgnoreCase(userType)) {
                Nurse nurse = db.nurseDAO().getNurseByUsername(username);
                runOnUiThread(() -> {
                    if (nurse != null) {
                        currentUser = nurse;
                        populateUI();
                    }
                });
            }
        });
    }

    private void initializeViews() {
        // Corrected IDs based on your XML
        profileName = findViewById(R.id.textView_firstName);
        profileSurname = findViewById(R.id.textView_lastName);
        profileUsername = findViewById(R.id.textView_username);
        profilePassword = findViewById(R.id.textView_password);

        // Student-specific views
        studentNumber = findViewById(R.id.textView_studentNo);
        medicationRequirement = findViewById(R.id.textView_medicationReq);
        foodRequirement = findViewById(R.id.textView_foodReq);
        studentFieldsGroup = findViewById(R.id.group_student_fields);

        // Nurse-specific view
        staffNumber = findViewById(R.id.textView_staffNo);
        nurseFieldsGroup = findViewById(R.id.group_nurse_fields);

        // Buttons
        updateButton = findViewById(R.id.button_update);
        deleteButton = findViewById(R.id.button_delete);
    }

    private void populateUI() {
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            profileName.setText(student.getStuName());
            profileSurname.setText(student.getStuSurname());
            profileUsername.setText(student.getUserName());
            studentNumber.setText(student.getStuNum());
            medicationRequirement.setText(student.getMedReq());
            foodRequirement.setText(student.getFoodReq());

            // Show student fields and hide nurse fields
            studentFieldsGroup.setVisibility(View.VISIBLE);
            nurseFieldsGroup.setVisibility(View.GONE);

        } else if (currentUser instanceof Nurse) {
            Nurse nurse = (Nurse) currentUser;
            profileName.setText(nurse.getEmpName());
            profileSurname.setText(nurse.getEmpSurname());
            profileUsername.setText(nurse.getEmpUserName());
            staffNumber.setText(nurse.getEmpNum());

            // Show nurse fields and hide student fields
            nurseFieldsGroup.setVisibility(View.VISIBLE);
            studentFieldsGroup.setVisibility(View.GONE);
        }
    }

    private void openUpdateActivity() {
        if (currentUser == null) return;

        Intent intent = new Intent(this, UpdateProfileActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("USER_TYPE", userType);
        updateProfileLauncher.launch(intent);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.profile_delete_dialog, null);
        builder.setView(dialogView);

        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();


        negativeButton.setOnClickListener(v -> dialog.dismiss());

        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteUserFromDatabase();
        });
    }

    private void deleteUserFromDatabase() {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            if (currentUser instanceof Student) {
                db.studentDAO().deleteStudent((Student) currentUser);
            } else if (currentUser instanceof Nurse) {
                db.nurseDAO().deleteNurse((Nurse) currentUser);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        });
    }
}