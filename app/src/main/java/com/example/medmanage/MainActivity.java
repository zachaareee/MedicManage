package com.example.medmanage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Login Button
        findViewById(R.id.button2).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        // Signup Button - shows dialog
        findViewById(R.id.button5).setOnClickListener(v -> {
            showSignUpDialog();
        });
    }

    private void showSignUpDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sign Up");

            // Inflate custom dialog layout
            View dialogView = getLayoutInflater().inflate(R.layout.dialogue_signup, null);
            builder.setView(dialogView);
    /*
            // Initialize input fields
            EditText etUserType = dialogView.findViewById(R.id.etUserType);
            EditText etId = dialogView.findViewById(R.id.etId);
            EditText etFirstName = dialogView.findViewById(R.id.etFirstName);
            EditText etSurname = dialogView.findViewById(R.id.etSurname);
            EditText etUsername = dialogView.findViewById(R.id.etUsername);
            EditText etMedReq = dialogView.findViewById(R.id.etMedReq);
            EditText etFoodReq = dialogView.findViewById(R.id.etFoodReq);

            builder.setPositiveButton("Sign Up", (dialog, which) -> {
                handleSignUp(
                        etUserType.getText().toString(),
                        etId.getText().toString(),
                        etFirstName.getText().toString(),
                        etSurname.getText().toString(),
                        etUsername.getText().toString(),
                        etMedReq.getText().toString(),
                        etFoodReq.getText().toString()
                );
            });

            builder.setNegativeButton("Cancel", null);
            builder.create().show();

     */
        } catch (Exception e) {
            showSnackbar("Error creating signup dialog: " + e.getMessage());
        }


        }



    private void handleSignUp(String userType, String id, String firstName,
                              String surname, String username, String medReq, String foodReq) {
       /* try {
            // Validate inputs
            if (firstName.isEmpty() || surname.isEmpty() || username.isEmpty()) {
                throw new IllegalArgumentException("All fields are required");
            }

            int userId = Integer.parseInt(id);
            View rootView = findViewById(android.R.id.content);

            if (userType.equalsIgnoreCase("student")) {
                Student student = new Student();
                student.setStuNum(userId);
                student.setStuName(firstName);
                student.setStuSurname(surname);
                student.setUserName(username);
                student.setMedRequirement(medReq);
                student.setFoodReq(foodReq);

                userViewModel.insertStudent(student);
                showSnackbar("Student registered successfully!");
            }
            else if (userType.equalsIgnoreCase("nurse")) {
                Nurse nurse = new Nurse();
                nurse.setEmpNum(userId);
                nurse.setEmpName(firstName);
                nurse.setEmpSurname(surname);
                nurse.setEmpUserName(username);

                userViewModel.insertNurse(nurse);
                showSnackbar("Nurse registered successfully!");
            } else {
                throw new IllegalArgumentException("Invalid user type. Use 'student' or 'nurse'");
            }
        } catch (NumberFormatException e) {
            showSnackbar("Invalid ID format. Please enter numbers only");
        } catch (Exception e) {
            showSnackbar(e.getMessage());
        }

        */
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    /*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

     */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Handle settings action
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

 */
}