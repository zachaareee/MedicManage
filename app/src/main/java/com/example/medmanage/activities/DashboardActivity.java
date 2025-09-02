package com.example.medmanage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeTextView;
    private Button logoutButton;
    private Button viewFoodButton;
    private Button medicationButton;
    private Button appointmentButton;
    private Button reviewAppointmentButton;
    private Button viewProfile;
    private Button scheduleAppointmentButton;
    private int loggedInStudentId = -1;

    @SuppressLint("SetTextII18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        welcomeTextView = findViewById(R.id.welcomeTextView);
        logoutButton = findViewById(R.id.logoutButton);
        viewFoodButton = findViewById(R.id.viewFoodButton);
        viewProfile = findViewById(R.id.profiledetails2);
        medicationButton = findViewById(R.id.medicationButton);
        appointmentButton = findViewById(R.id.appointmentButton);
        reviewAppointmentButton = findViewById(R.id.reviewAppointmentButton);
        scheduleAppointmentButton = findViewById(R.id.scheduleAppointmentButton);

        Intent intent = getIntent();
        String username = intent.getStringExtra("USERNAME");
        String userType = intent.getStringExtra("USER_TYPE");
        loggedInStudentId = intent.getIntExtra("STUDENT_ID", -1);

        if (username != null) {
            String welcomeMessage = getString(R.string.welcome_text) + " " + username + "!";
            welcomeTextView.setText(welcomeMessage);
        } else {
            welcomeTextView.setText(R.string.welcome_text);
        }

        if ("student".equalsIgnoreCase(userType)) {
            appointmentButton.setVisibility(View.VISIBLE);
            reviewAppointmentButton.setVisibility(View.GONE);
            scheduleAppointmentButton.setVisibility(View.VISIBLE);
        } else if ("nurse".equalsIgnoreCase(userType)) {
            appointmentButton.setVisibility(View.GONE);
            reviewAppointmentButton.setVisibility(View.VISIBLE);
            scheduleAppointmentButton.setVisibility(View.GONE);
        } else {
            appointmentButton.setVisibility(View.GONE);
            reviewAppointmentButton.setVisibility(View.GONE);
            scheduleAppointmentButton.setVisibility(View.GONE);
        }

        viewFoodButton.setOnClickListener(v -> {
            Intent foodIntent = new Intent(DashboardActivity.this, FoodViewActivity.class);
            foodIntent.putExtra("USER_TYPE", userType);
            startActivity(foodIntent);
        });

        logoutButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
        });

        viewProfile.setOnClickListener(view -> {
            Intent viewIntent = new Intent(DashboardActivity.this, ViewProfileDetailsActivity.class);
            viewIntent.putExtra("USERNAME", username);
            viewIntent.putExtra("USER_TYPE", userType);
            startActivity(viewIntent);
        });

        medicationButton.setOnClickListener(view -> {
            Intent medIntent = new Intent(DashboardActivity.this, ViewMedicationActivity.class);
            medIntent.putExtra("USERNAME", username);
            medIntent.putExtra("USER_TYPE", userType);
            startActivity(medIntent);
        });

        appointmentButton.setOnClickListener(view -> {
            if (loggedInStudentId != -1) {
                Intent appointmentIntent = new Intent(DashboardActivity.this, ViewAppointmentActivity.class);
                appointmentIntent.putExtra(ViewAppointmentActivity.STUDENT_ID_EXTRA, loggedInStudentId);
                startActivity(appointmentIntent);
            }
        });

        scheduleAppointmentButton.setOnClickListener(view -> {
            if (loggedInStudentId != -1) {
                Intent scheduleIntent = new Intent(DashboardActivity.this, ScheduleAppointmentActivity.class);
                scheduleIntent.putExtra(ScheduleAppointmentActivity.STUDENT_ID_EXTRA, loggedInStudentId);
                startActivity(scheduleIntent);
            } else {
                Toast.makeText(DashboardActivity.this, "Error: Student not identified.", Toast.LENGTH_SHORT).show();
            }
        });

        reviewAppointmentButton.setOnClickListener(view -> {
            Intent reviewIntent = new Intent(DashboardActivity.this, ReviewAppointmentActivity.class);
            startActivity(reviewIntent);
        });
    }
}