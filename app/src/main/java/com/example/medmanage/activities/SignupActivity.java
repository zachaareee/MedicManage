
package com.example.medmanage.activities;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

import java.util.concurrent.ExecutorService;

public class SignupActivity extends AppCompatActivity {

    // UI Elements
    private RadioGroup userTypeRadioGroup;
    private RadioButton studentRadioButton, nurseRadioButton;

    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private TextView FoodReq;
    private EditText staffNoEditText, studentNoEditText, medicationReqEditText;
    private LinearLayout studentFieldsLayout;
    private RadioGroup foodReqRadioGroup;
    private Button confirmButton, cancelButton;
    private boolean isPasswordVisible = false;
    private ImageView passwordToggle;


    // Database
    private databaseMedicManage db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_create);
        initializeViews();


        // This makes sure `db` is not null when needed.
        db =databaseMedicManage.getDatabase(getApplicationContext());
        setupListeners();
        if(savedInstanceState==null){
            studentRadioButton.setChecked(true);
        }
    }

    private void initializeViews() {
        userTypeRadioGroup = findViewById(R.id.user_type_group);
        studentRadioButton = findViewById(R.id.student_radio_btn);
        nurseRadioButton = findViewById(R.id.nurse_radio_btn);
        firstNameEditText = findViewById(R.id.editText_firstName);
        lastNameEditText = findViewById(R.id.editText_lastName);
        usernameEditText = findViewById(R.id.editText_username);
        passwordEditText = findViewById(R.id.editText_password);
        staffNoEditText = findViewById(R.id.editText_staffNo);
        studentFieldsLayout = findViewById(R.id.layout_studentFields);
        studentNoEditText = findViewById(R.id.editText_studentNo);
        medicationReqEditText = findViewById(R.id.editText_medicationReq);
        foodReqRadioGroup = findViewById(R.id.radioGroup_foodReq);
        confirmButton = findViewById(R.id.button_confirm);
        cancelButton = findViewById(R.id.button_cancel);
        passwordToggle = findViewById(R.id.password_toggle);
        FoodReq = findViewById(R.id.textView_foodReq);
    }

    private void setupListeners() {
        userTypeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateUiForUserType(checkedId);
        });

        confirmButton.setOnClickListener(v -> {
            registerUser();
        });

        cancelButton.setOnClickListener(v -> {

           showCancelConfirmationDialog();
        });
        passwordToggle.setOnClickListener(v -> togglePasswordVisibility());
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

    private void updateUiForUserType(int checkedId) {
        if (checkedId == R.id.student_radio_btn) {
            studentFieldsLayout.setVisibility(View.VISIBLE);
            medicationReqEditText.setVisibility(View.VISIBLE);
            foodReqRadioGroup.setVisibility(View.VISIBLE);
            staffNoEditText.setVisibility(View.GONE);
            FoodReq.setVisibility(View.VISIBLE);

        } else if (checkedId == R.id.nurse_radio_btn) {
            studentFieldsLayout.setVisibility(View.GONE);
            medicationReqEditText.setVisibility(View.GONE);
            foodReqRadioGroup.setVisibility(View.GONE);
            studentFieldsLayout.setVisibility(View.GONE);
            staffNoEditText.setVisibility(View.VISIBLE);
            FoodReq.setVisibility(View.GONE);
        }
    }

    private void registerUser() {
        // ... (rest of the method is unchanged)
        int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();

        if (selectedUserTypeId == -1) {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure db is not null before proceeding
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

    private void registerNewStudent() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String studentNoStr = studentNoEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String medicationReq = medicationReqEditText.getText().toString().trim();
        String foodReq = ((RadioButton) findViewById(foodReqRadioGroup.getCheckedRadioButtonId())).getText().toString();

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
    // Add this entire method to your SignupActivity.java file

    private void showCancelConfirmationDialog() {
        // 1. Create a builder for the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Get the layout inflater to use your custom XML
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_cancel_dialog, null);

        // 3. Set the custom view for the dialog
        builder.setView(dialogView);

        // Find the buttons inside your custom dialog layout
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        // Create the dialog object so you can show and dismiss it
        final AlertDialog dialog = builder.create();

        // 4. Set click listeners for the dialog buttons
        // "OK" button will close the activity
        positiveButton.setOnClickListener(v -> {
            dialog.dismiss(); // Close the dialog
            finish();         // Close the SignupActivity
        });

        // "No" button will just close the dialog
        negativeButton.setOnClickListener(v -> {
            dialog.dismiss(); // Close the dialog
        });

        // 5. Show the dialog
        dialog.show();
    }
}