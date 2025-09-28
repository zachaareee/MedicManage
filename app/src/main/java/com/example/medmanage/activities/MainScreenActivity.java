package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;

public class MainScreenActivity extends AppCompatActivity {

    // Declare the UI elements from your XML file
    private Button loginButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Link this activity to your mainscreen.xml layout file
        setContentView(R.layout.login_screen);

        // Initialize the buttons by finding them in the layout by their ID
        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);

        // Set a click listener for the Login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the LoginActivity
                Intent intent = new Intent(MainScreenActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // Set a click listener for the Sign Up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the SignUpActivity
                // Note: You will need to create a SignUpActivity for this to work
                Intent intent = new Intent(MainScreenActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}