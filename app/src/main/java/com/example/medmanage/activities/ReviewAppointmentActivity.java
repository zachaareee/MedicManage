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
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.Locale;
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
            String displayText = String.format("Student Number: %s ", item.student.getStuNum());
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




    private void showUpdateConfirmationDialog(final Appointment appointmentToUpdate, final AlertDialog editDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        // Use the new layout file
        View dialogView = inflater.inflate(R.layout.appnt_review_changes_dialog, null);
        builder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);

        // This is the second (confirmation) dialog
        final AlertDialog confirmDialog = builder.create();

        // Make it look nice like the delete dialog
        if (confirmDialog.getWindow() != null) {
            confirmDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        yesButton.setOnClickListener(v -> {
            // User clicked "Yes", so now we update the DB
            // We pass both dialogs so the update method can close them
            updateAppointmentInDb(appointmentToUpdate, editDialog, confirmDialog);
        });

        noButton.setOnClickListener(v -> {
            confirmDialog.dismiss();
        });

        confirmDialog.show();
    }

    private void updateAppointmentInDb(Appointment appointment, AlertDialog... dialogsToDismiss) {
        databaseExecutor.execute(() -> {
            appDb.appointmentDAO().update(appointment);
            runOnUiThread(() -> {
                Toast.makeText(this, "Appointment updated successfully", Toast.LENGTH_SHORT).show();
                // Dismiss all passed dialogs
                for (AlertDialog dialog : dialogsToDismiss) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
                // LiveData will automatically refresh the view
            });
        });
    }

    private void showDeleteConfirmationDialog(final Appointment appointmentToDelete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.appnt_delete_dialog_nur, null);
        builder.setView(dialogView);


        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);

        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        yesButton.setOnClickListener(v -> {
            deleteAppointmentFromDb(appointmentToDelete);
            dialog.dismiss();
        });

        noButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // 6. Show the dialog
        dialog.show();
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


        // Make fields non-editable by keyboard
        dateEditText.setFocusable(false);
        dateEditText.setClickable(true);

        timeEditText.setFocusable(false);
        timeEditText.setClickable(true);

        // Show pickers on click
        dateEditText.setOnClickListener(v -> showDatePicker(dateEditText));
        timeEditText.setOnClickListener(v -> showTimePicker(timeEditText));


        final AlertDialog editDialog = builder.create();

        if (editDialog.getWindow() != null) {
            editDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        saveButton.setOnClickListener(v -> {
            String newDate = dateEditText.getText().toString().trim();
            String newTime = timeEditText.getText().toString().trim();


            if (TextUtils.isEmpty(newDate) || TextUtils.isEmpty(newTime)) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Set the new values on the object
            appointmentToEdit.setDate(newDate);
            appointmentToEdit.setTime(newTime);

            showUpdateConfirmationDialog(appointmentToEdit, editDialog);
        });

        cancelButton.setOnClickListener(v -> editDialog.dismiss());
        editDialog.show();
    }
    private void showDatePicker(final EditText dateEditText) {
        final Calendar calendar = Calendar.getInstance();

        // Try to parse existing date to pre-set the picker
        try {
            String[] dateParts = dateEditText.getText().toString().split("-");
            if (dateParts.length == 3) {
                calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[0]));
                calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1); // Month is 0-indexed
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[2]));
            }
        } catch (Exception e) {
            // Ignore if parsing fails, just use current date
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Format the date as yyyy-mm-dd
                    String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year1, (monthOfYear + 1), dayOfMonth);
                    dateEditText.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker(final EditText timeEditText) {
        final Calendar calendar = Calendar.getInstance();

        // Try to parse existing time to pre-set the picker
        try {
            String[] timeParts = timeEditText.getText().toString().split(":");
            if (timeParts.length == 2) {
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeParts[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
            }
        } catch (Exception e) {
            // Ignore if parsing fails, just use current time
        }

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {



                    boolean isTimeValid = true;
                    if (hourOfDay < 8 || hourOfDay > 16 || (hourOfDay == 16 && minuteOfHour > 0)) {
                        isTimeValid = false;
                    }

                    if (isTimeValid) {
                        // Time is valid, update the text
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                        timeEditText.setText(selectedTime);
                    } else {
                        // Time is invalid, show an error and do not update the text
                        Toast.makeText(ReviewAppointmentActivity.this, "Please select a time between 08:00 and 16:00", Toast.LENGTH_LONG).show();
                    }


                }, hour, minute, true); // true for 24-hour view
        timePickerDialog.show();
    }
}