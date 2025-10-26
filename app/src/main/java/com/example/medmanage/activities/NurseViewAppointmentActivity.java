package com.example.medmanage.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.AppointmentWithStudent;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class NurseViewAppointmentActivity extends AppCompatActivity {

    private Spinner appointmentSpinner;
    private ConstraintLayout appointmentSpinnerContainer;
    private LinearLayout appointmentDetailsContainer;
    private TextView medicationValue, foodValue, dateValue, timeValue;
    private TextView noAppointmentText;
    private MaterialButton negativeButton;

    private databaseMedicManage appDb;
    private List<AppointmentWithStudent> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appnt_view_nurse);
        initializeViews();
        appDb = databaseMedicManage.getDatabase(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllAppointments();
    }

    private void initializeViews() {
        appointmentSpinner = findViewById(R.id.appointmentSpinner);
        appointmentSpinnerContainer = findViewById(R.id.appointmentSpinnerContainer);
        appointmentDetailsContainer = findViewById(R.id.appointmentDetailsContainer);
        medicationValue = findViewById(R.id.medicationValue);
        foodValue = findViewById(R.id.foodValue);
        dateValue = findViewById(R.id.dateValue);
        timeValue = findViewById(R.id.timeValue);
        noAppointmentText = findViewById(R.id.noAppointmentText);
        negativeButton = findViewById(R.id.negativeButton);

        negativeButton.setOnClickListener(v -> showQuitConfirmationDialog());
    }

    private void loadAllAppointments() {
        // Fetch ALL appointments for the spinner
        LiveData<List<AppointmentWithStudent>> liveData = appDb.appointmentDAO().getAllAppointmentsWithStudents();
        liveData.observe(this, appointments -> {
            if (appointments == null || appointments.isEmpty()) {
                showNoAppointmentsView();
            } else {
                this.appointmentList = appointments;
                showAppointmentView();
                setupSpinner(appointments);
            }
        });
    }

    private void setupSpinner(List<AppointmentWithStudent> appointments) {
        if (isFinishing() || isDestroyed()) return;

        List<String> spinnerItems = new ArrayList<>();
        // Create a display string for each appointment in the list
        for (AppointmentWithStudent item : appointments) {
            String displayText = String.format("Student Number: %s ", item.student.getStuNum());
            spinnerItems.add(displayText);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.appnt_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appointmentSpinner.setAdapter(adapter);

        // Set a listener to update details when an item is selected
        appointmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (appointmentList != null && position < appointmentList.size()) {
                    updateDetailsView(appointmentList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally clear details if nothing is selected
                updateDetailsView(null);
            }
        });

        // Show the first appointment's details by default
        if (!appointments.isEmpty()) {
            updateDetailsView(appointments.get(0));
        }
    }

    private void updateDetailsView(AppointmentWithStudent item) {
        if (item != null) {
            medicationValue.setText(item.student.getMedReq());
            foodValue.setText(item.student.getFoodReq()); // Assuming Student model has getFoodReq()
            dateValue.setText(item.appointment.getDate());
            timeValue.setText(item.appointment.getTime());
        } else {
            // Clear the fields if no appointment is selected
            medicationValue.setText("");
            foodValue.setText("");
            dateValue.setText("");
            timeValue.setText("");
        }
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

        // Make the dialog window background transparent
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
    private void showAppointmentView() {
        noAppointmentText.setVisibility(View.GONE);
        appointmentSpinnerContainer.setVisibility(View.VISIBLE);
        appointmentDetailsContainer.setVisibility(View.VISIBLE);
    }

    private void showNoAppointmentsView() {
        noAppointmentText.setVisibility(View.VISIBLE);
        appointmentSpinnerContainer.setVisibility(View.GONE);
        appointmentDetailsContainer.setVisibility(View.GONE);
    }
}
