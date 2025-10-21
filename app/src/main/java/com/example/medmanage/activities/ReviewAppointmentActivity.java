package com.example.medmanage.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.AppointmentWithStudent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReviewAppointmentActivity extends AppCompatActivity {

    // UI Components
    private Spinner appointmentSpinner;
    private ConstraintLayout appointmentSpinnerContainer;
    private LinearLayout appointmentDetailsContainer;
    private TextView studentNumberValue, medicationValue, foodValue, dateValue, timeValue;
    private TextView noAppointmentText;
    private Button editButton, deleteButton;
    private Button negativeButton;

    // Database and Data
    private databaseMedicManage appDb;
    private ExecutorService databaseExecutor;
    private List<AppointmentWithStudent> appointmentList;
    private AppointmentWithStudent selectedAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appnt_review);

        appDb = databaseMedicManage.getDatabase(getApplicationContext());
        databaseExecutor = Executors.newSingleThreadExecutor();

        initializeViews();
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
        studentNumberValue = findViewById(R.id.studentNumberValue);
        medicationValue = findViewById(R.id.medicationValue);
        foodValue = findViewById(R.id.foodValue);
        dateValue = findViewById(R.id.dateValue);
        timeValue = findViewById(R.id.timeValue);
        noAppointmentText = findViewById(R.id.noAppointmentText);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        negativeButton = findViewById(R.id.negativeButton);

        editButton.setOnClickListener(v -> {
            if (selectedAppointment != null) {
                showEditDialog(selectedAppointment.appointment);
            } else {
                Toast.makeText(this, "Please select an appointment to edit", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedAppointment != null) {
                showDeleteConfirmationDialog(selectedAppointment.appointment);
            } else {
                Toast.makeText(this, "Please select an appointment to delete", Toast.LENGTH_SHORT).show();
            }
        });
        negativeButton.setOnClickListener(v -> {
            showQuitConfirmationDialog();

        });

    }

    private void loadAllAppointments() {
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
        for (AppointmentWithStudent item : appointments) {
            String displayText = String.format("ID: %s | Date: %s", item.student.getStuNum(), item.appointment.getDate());
            spinnerItems.add(displayText);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.appnt_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        appointmentSpinner.setAdapter(adapter);

        appointmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (appointmentList != null && position < appointmentList.size()) {
                    updateDetailsView(appointmentList.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateDetailsView(null);
            }
        });

        if (!appointments.isEmpty()) {
            updateDetailsView(appointments.get(0));
        }
    }

    private void updateDetailsView(AppointmentWithStudent item) {
        this.selectedAppointment = item;
        if (item != null) {
            // Convert the integer student number to a String before setting it
            studentNumberValue.setText(String.valueOf(item.student.getStuNum()));

            medicationValue.setText(item.student.getMedReq());
            foodValue.setText(item.student.getFoodReq());
            dateValue.setText(item.appointment.getDate());
            timeValue.setText(item.appointment.getTime());
        }
    }

    private void showEditDialog(final Appointment appointmentToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.appnt_edit, null);
        builder.setView(dialogView);

        final EditText dateEditText = dialogView.findViewById(R.id.dateEditText);
        final EditText timeEditText = dialogView.findViewById(R.id.timeEditText);
        final Button saveButton = dialogView.findViewById(R.id.saveButton);
        final Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        dateEditText.setText(appointmentToEdit.getDate());
        timeEditText.setText(appointmentToEdit.getTime());

        final AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String newDate = dateEditText.getText().toString().trim();
            String newTime = timeEditText.getText().toString().trim();

            if (TextUtils.isEmpty(newDate) || TextUtils.isEmpty(newTime)) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            appointmentToEdit.setDate(newDate);
            appointmentToEdit.setTime(newTime);
            updateAppointmentInDb(appointmentToEdit, dialog);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateAppointmentInDb(Appointment appointment, AlertDialog dialog) {
        databaseExecutor.execute(() -> {
            appDb.appointmentDAO().update(appointment);
            runOnUiThread(() -> {
                Toast.makeText(this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                // LiveData will automatically refresh the view
            });
        });
    }

    private void showDeleteConfirmationDialog(final Appointment appointmentToDelete) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Appointment")
                .setMessage("Are you sure you want to delete this appointment?")
                .setPositiveButton("Yes", (dialog, which) -> deleteAppointmentFromDb(appointmentToDelete))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteAppointmentFromDb(Appointment appointment) {
        databaseExecutor.execute(() -> {
            appDb.appointmentDAO().deleteAppointment(appointment);
            // Also delete associated links if necessary
            appDb.appointmentMedicationDAO().deleteLinksForAppointment(appointment.getAppointmentNum());
            runOnUiThread(() -> {
                Toast.makeText(this, "Appointment deleted", Toast.LENGTH_SHORT).show();
                // LiveData will auto-refresh the list
            });
        });
    }
    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.general_confirm_dialog, null);
        builder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);

        final AlertDialog dialog = builder.create();
        dialog.show();

        yesButton.setOnClickListener(v -> finish());
        noButton.setOnClickListener(v -> dialog.dismiss());
    }

    private void showAppointmentView() {
        noAppointmentText.setVisibility(View.GONE);
        appointmentSpinnerContainer.setVisibility(View.VISIBLE);
        appointmentDetailsContainer.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
        deleteButton.setVisibility(View.VISIBLE);
    }

    private void showNoAppointmentsView() {
        noAppointmentText.setVisibility(View.VISIBLE);
        appointmentSpinnerContainer.setVisibility(View.GONE);
        appointmentDetailsContainer.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
    }
}