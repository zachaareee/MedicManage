package com.example.medmanage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button logoutButton;
    private Button viewFoodButton; // Button to open the food list
    private Button medicationButton;
    private Button appointmentButton;
    private Button viewProfile;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);
        viewFoodButton = findViewById(R.id.viewFoodButton);
        viewProfile = findViewById(R.id.profiledetails);
        medicationButton =findViewById(R.id.medicationButton);


        // Get user data from the Intent that started this activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String userType = intent.getStringExtra("USER_TYPE");

        // Display welcome message
        if (username != null) {
            // Correctly get the string resource first
            String welcomeMessage = getString(R.string.welcome_text) + " " + username + "!";
            welcomeTextView.setText(welcomeMessage);
        } else {
            welcomeTextView.setText(R.string.welcome_text);
        }
        // Set up listener for the "View Food List" button
        viewFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent foodIntent = new Intent(DashboardActivity.this, FoodViewActivity.class);
                // Pass the user type to the FoodViewActivity
                foodIntent.putExtra("USER_TYPE", userType);
                startActivity(foodIntent);
            }
        });

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(loginIntent);
                finish();
            }
        });
        // ... inside the onCreate method

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent = new Intent(DashboardActivity.this, ViewProfileDetailsActivity.class);
                // Add the username and userType to the intent
                viewIntent.putExtra("USERNAME", username);
                viewIntent.putExtra("USER_TYPE", userType);
                startActivity(viewIntent);
            }
        });
        medicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent medIntent = new Intent(DashboardActivity.this,ViewMedicationActivity.class);
                medIntent.putExtra("USERNAME", username);
                medIntent.putExtra("USER_TYPE", userType);
                startActivity(medIntent);
            }
        });

    }
}