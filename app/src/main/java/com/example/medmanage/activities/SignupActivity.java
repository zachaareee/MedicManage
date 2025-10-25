package com.example.medmanage.activities;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class SignupActivity extends AppCompatActivity {

    // UI Elements
    private RadioGroup userTypeRadioGroup;
    private RadioButton studentRadioButton;
    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private EditText staffNoEditText, studentNoEditText;
    private LinearLayout studentFieldsLayout;
    private AutoCompleteTextView medicationAutoComplete;
    private RadioGroup foodReqRadioGroup;
    private Button confirmButton, cancelButton;
    private ImageView passwordToggle;
    private boolean isPasswordVisible = false;

    // Database and ViewModel
    private databaseMedicManage db;
    private UserViewModel userViewModel; // Added for dropdown data

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_create);

        // Initialize ViewModel first
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        initializeViews();
        db = databaseMedicManage.getDatabase(getApplicationContext());
        setupListeners();
        setupMedicationDropdown();

        if (savedInstanceState == null) {
            studentRadioButton.setChecked(true);
            updateUiForUserType(R.id.student_radio_btn); // Set initial UI state
        }
    }

    private void initializeViews() {
        userTypeRadioGroup = findViewById(R.id.user_type_group);
        studentRadioButton = findViewById(R.id.student_radio_btn);
        firstNameEditText = findViewById(R.id.editText_firstName);
        lastNameEditText = findViewById(R.id.editText_lastName);
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        passwordToggle = findViewById(R.id.password_toggle);

        // Student-specific layout and fields
        studentFieldsLayout = findViewById(R.id.layout_studentFields);
        studentNoEditText = findViewById(R.id.editText_studentNo);
        medicationAutoComplete = findViewById(R.id.autoComplete_medication);
        foodReqRadioGroup = findViewById(R.id.radioGroup_foodReq);

        // Nurse-specific field
        staffNoEditText = findViewById(R.id.editText_staffNo);

        // Buttons
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.negativeButton); // Corrected ID
    }

    private void setupListeners() {
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> updateUiForUserType(checkedId));

        confirmButton.setOnClickListener(v -> registerUser());
        cancelButton.setOnClickListener(v -> showCancelConfirmationDialog());
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
    }

    /**
     * Fetches medication names from the database and populates the dropdown.
     */
    private void setupMedicationDropdown() {
        userViewModel.getAllMedications().observe(this, medications -> {
            if (medications != null) {
                // Get distinct medication names
                List<String> medNames = medications.stream()
                        .map(Medication::getMedName)
                        .distinct()
                        .collect(Collectors.toList());

                // Add a "None" option at the beginning
                List<String> optionsWithNone = new ArrayList<>();
                optionsWithNone.add("None");
                optionsWithNone.addAll(medNames);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        optionsWithNone
                );
                medicationAutoComplete.setAdapter(adapter);
            }
        });
    }

    /**
     * Toggles the visibility of student-specific and nurse-specific fields.
     */
    private void updateUiForUserType(int checkedId) {
        if (checkedId == R.id.student_radio_btn) {
            studentFieldsLayout.setVisibility(View.VISIBLE);
            staffNoEditText.setVisibility(View.GONE);
        } else { // Nurse is selected
            studentFieldsLayout.setVisibility(View.GONE);
            staffNoEditText.setVisibility(View.VISIBLE);
        }
    }

    private void registerNewStudent() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String studentNoStr = studentNoEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        // Get text from the AutoCompleteTextView
        String medicationReq = medicationAutoComplete.getText().toString().trim();

        int selectedFoodRadioId = foodReqRadioGroup.getCheckedRadioButtonId();
        if (selectedFoodRadioId == -1) {
            Toast.makeText(this, "Please select a food requirement option", Toast.LENGTH_SHORT).show();
            return;
        }
        String foodReq = ((RadioButton) findViewById(selectedFoodRadioId)).getText().toString();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(studentNoStr) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all student fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentNo;
        try {
            studentNo = Integer.parseInt(studentNoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid student number", Toast.LENGTH_SHORT).show();
            return;
        }

        Student newStudent = new Student(studentNo, firstName, lastName, username, foodReq, medicationReq, password);
        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            db.studentDAO().addStudent(newStudent);
            runOnUiThread(() -> {
                Toast.makeText(this, "Student registered successfully!", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }



    private void registerUser() {
        int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();
        if (selectedUserTypeId == -1) {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (db == null) {
            Toast.makeText(this, "Database is not yet ready. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedUserTypeId == R.id.student_radio_btn) {
            registerNewStudent();
        } else {
            registerNewNurse();
        }
    }

    private void registerNewNurse() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String staffNoStr = staffNoEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(staffNoStr) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all nurse fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int staffNo;
        try {
            staffNo = Integer.parseInt(staffNoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid staff number", Toast.LENGTH_SHORT).show();
            return;
        }

        Nurse newNurse = new Nurse(staffNo, firstName, lastName, username, password);
        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            db.nurseDAO().addNurse(newNurse);
            runOnUiThread(() -> {
                Toast.makeText(this, "Nurse registered successfully!", Toast.LENGTH_LONG).show();
                finish();
            });
        });
    }

    private void togglePasswordVisibility() {
        // ... (This method is correct as is)
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

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_cancel_dialog, null);
        builder.setView(dialogView);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        final AlertDialog dialog = builder.create();
        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        negativeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}