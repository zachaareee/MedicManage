package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;



/**
 * DashboardActivity is the landing screen after successful login.
 * It displays a welcome message based on the logged-in user and provides a logout option.
 */
public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);

        // Get user data from the Intent
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String userType = intent.getStringExtra("USER_TYPE");

        // Display welcome message
        if (username != null && userType != null) {
            welcomeTextView.setText("Welcome, " + username + " (" + userType + ")!");
        } else {
            welcomeTextView.setText("Welcome!");
        }

        // Set up logout button click listener
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to LoginActivity
                Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear activity stack
                startActivity(loginIntent);
                finish(); // Close DashboardActivity
            }
        });
    }
}