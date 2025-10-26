package com.example.medmanage.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;
import com.example.medmanage.view_model.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class UpdateProfileActivity extends AppCompatActivity {

    // Common UI Elements
    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private Button confirmButton, cancelButton;
    private ImageView passwordToggle;

    // Nurse-Specific UI
    private EditText staffNoEditText;
    private Group nurseFieldsGroup;

    // Student-Specific UI
    private EditText studentNoEditText;
    private AutoCompleteTextView medicationAutoComplete;
    private ImageView medicationDropdownArrow;
    private RadioGroup foodReqRadioGroup;
    private Group studentFieldsGroup;

    // Database and ViewModel
    private databaseMedicManage db;
    private UserViewModel userViewModel;
    private Object currentUser;
    private String username;
    private String userType;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update);

        db = databaseMedicManage.getDatabase(getApplicationContext());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initializeViews();
        setupMedicationDropdown();

        username = getIntent().getStringExtra("USERNAME");
        userType = getIntent().getStringExtra("USER_TYPE");

        fetchUser();

        confirmButton.setOnClickListener(v -> showUpdateConfirmationDialog());
        cancelButton.setOnClickListener(v -> showQuitConfirmationDialog());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
        medicationDropdownArrow.setOnClickListener(v -> medicationAutoComplete.showDropDown());
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


    private void showUpdateConfirmationDialog() {
        // Create a builder for the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_update_dialog, null);
        builder.setView(dialogView);

        // Find buttons inside the dialog layout
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        // Create the AlertDialog object
        final AlertDialog dialog = builder.create();

        // Make the dialog window background transparent to show the rounded corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set button listeners
        positiveButton.setOnClickListener(v -> {
            // When "Yes" is clicked, perform the update and close the dialog
            updateUser();
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            // When "Cancel" is clicked, just close the dialog
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

    /**
     * Gathers data from the UI and updates the user's record in the database.
     * This method is now called from the dialog's positive button.
     */
    // In UpdateProfileActivity.java

    private void updateUser() {
        final String firstName = firstNameEditText.getText().toString().trim();
        final String lastName = lastNameEditText.getText().toString().trim();
        final String usernameInput = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {

            try {

                if (currentUser instanceof Student) {
                    Student student = (Student) currentUser;
                    String selectedMedication = medicationAutoComplete.getText().toString();
                    String foodReq = ((RadioButton) findViewById(foodReqRadioGroup.getCheckedRadioButtonId())).getText().toString();

                    student.setStuName(firstName);
                    student.setStuSurname(lastName);
                    student.setUserName(usernameInput);
                    student.setPassword(password);
                    student.setFoodReq(foodReq);
                    student.setMedReq("None".equalsIgnoreCase(selectedMedication) ? "No" : selectedMedication);

                    db.studentDAO().updateStudent(student);

                } else if (currentUser instanceof Nurse) {
                    Nurse nurse = (Nurse) currentUser;
                    nurse.setEmpName(firstName);
                    nurse.setEmpSurname(lastName);
                    nurse.setEmpUserName(usernameInput);
                    nurse.setPassword(password);

                    db.nurseDAO().updateNurse(nurse);
                }

                runOnUiThread(() -> {
                    Toast.makeText(UpdateProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("UPDATED_USERNAME", usernameInput);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                });

            } catch (android.database.sqlite.SQLiteConstraintException e) {
                runOnUiThread(() -> {
                    usernameEditText.setError("This username is already taken");
                    Toast.makeText(UpdateProfileActivity.this, "This username is already taken. Please choose another.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        // Use the general confirmation dialog layout
        View dialogView = inflater.inflate(R.layout.general_confirm_dialog, null);
        builder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);
        final TextView messageTextView = dialogView.findViewById(R.id.confirmationMessageTextView);

        messageTextView.setText(R.string.quit_dialog);

        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        yesButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // Quit the activity
        });
        noButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


    private void initializeViews() {
        firstNameEditText = findViewById(R.id.editText_firstName);
        lastNameEditText = findViewById(R.id.editText_lastName);
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        passwordToggle = findViewById(R.id.password_toggle);
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.button_cancel);
        staffNoEditText = findViewById(R.id.editText_staffNo);
        nurseFieldsGroup = findViewById(R.id.group_nurse_fields);
        studentNoEditText = findViewById(R.id.editText_studentNo);
        medicationAutoComplete = findViewById(R.id.autoComplete_medication);
        foodReqRadioGroup = findViewById(R.id.radioGroup_foodReq);
        studentFieldsGroup = findViewById(R.id.group_student_fields);
        medicationDropdownArrow = findViewById(R.id.medicationDropdownArrow);
    }

    private void setupMedicationDropdown() {
        // Get the predefined list from your string resources
        String[] illnesses = getResources().getStringArray(R.array.illness_array);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                illnesses
        );

        medicationAutoComplete.setAdapter(adapter);
    }

    private void fetchUser() {
        db.databaseWriteExecutor.execute(() -> {
            if ("student".equalsIgnoreCase(userType)) {
                Student student = db.studentDAO().getStudentByUsername(username);
                if (student != null) {
                    currentUser = student;
                    runOnUiThread(() -> populateFieldsStudent(student));
                }
            } else if ("nurse".equalsIgnoreCase(userType)) {
                Nurse nurse = db.nurseDAO().getNurseByUsername(username);
                if (nurse != null) {
                    currentUser = nurse;
                    runOnUiThread(() -> populateFieldsNurse(nurse));
                }
            }
        });
    }

    private void populateFieldsStudent(Student student) {
        nurseFieldsGroup.setVisibility(View.GONE);
        studentFieldsGroup.setVisibility(View.VISIBLE);
        firstNameEditText.setText(student.getStuName());
        lastNameEditText.setText(student.getStuSurname());
        studentNoEditText.setText(String.valueOf(student.getStuNum()));
        studentNoEditText.setEnabled(false);
        usernameEditText.setText(student.getUserName());
        passwordEditText.setText(student.getPassword());
        String savedMedication = student.getMedReq();
        if (savedMedication == null || savedMedication.isEmpty() || "No".equalsIgnoreCase(savedMedication)) {
            medicationAutoComplete.setText("None", false);
        } else {
            medicationAutoComplete.setText(savedMedication, false);
        }
        if ("Yes".equalsIgnoreCase(student.getFoodReq())) {
            ((RadioButton) findViewById(R.id.radioButton_foodYes)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioButton_foodNo)).setChecked(true);
        }
    }

    private void populateFieldsNurse(Nurse nurse) {
        studentFieldsGroup.setVisibility(View.GONE);
        nurseFieldsGroup.setVisibility(View.VISIBLE);
        firstNameEditText.setText(nurse.getEmpName());
        lastNameEditText.setText(nurse.getEmpSurname());
        staffNoEditText.setText(String.valueOf(nurse.getEmpNum()));
        staffNoEditText.setEnabled(false);
        usernameEditText.setText(nurse.getEmpUserName());
        passwordEditText.setText(nurse.getPassword());
    }
}