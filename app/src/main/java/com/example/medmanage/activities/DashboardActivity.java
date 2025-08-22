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

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);
        viewFoodButton = findViewById(R.id.viewFoodButton);

        // Get user data from the Intent that started this activity
        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String userType = intent.getStringExtra("USER_TYPE");

        // Display welcome message
        if (username != null) {
            welcomeTextView.setText("@string/welcome" + username + "!");
        } else {
            welcomeTextView.setText("@string/welcome");
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
    }
}