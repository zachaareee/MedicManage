package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;

public class MainScreenActivity extends AppCompatActivity {


    private Button loginButton;
    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dashboard_login);


        loginButton = findViewById(R.id.loginButton);
        signUpButton = findViewById(R.id.signUpButton);



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

                Intent intent = new Intent(MainScreenActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}