package com.example.medmanage.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Student;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class StudentViewAppointmentActivity extends AppCompatActivity {

    public static final String STUDENT_ID_EXTRA = "student_id_extra";
    public static final String SHOW_CANCEL_BUTTON_EXTRA = "show_cancel_button";
    private TextView medicationValue, foodValue, dateValue, timeValue, noAppointmentText;
    private LinearLayout appointmentDetailsContainer;
    private Button cancelAppointmentButton, negativeButton;


    private databaseMedicManage appDb;
    private ExecutorService databaseExecutor;
    private int currentStudentId;
    private Appointment currentAppointment;
    private boolean shouldShowCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appnt_view_stu);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(STUDENT_ID_EXTRA)) {
            currentStudentId = intent.getIntExtra(STUDENT_ID_EXTRA, -1);
            shouldShowCancelButton = intent.getBooleanExtra(SHOW_CANCEL_BUTTON_EXTRA, false);
        }

        // If no valid student ID found, show error and finish
        if (currentStudentId == -1) {
            Toast.makeText(this, "Error: User not identified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize database and executor
        appDb = databaseMedicManage.getDatabase(getApplicationContext());
        databaseExecutor = databaseMedicManage.databaseWriteExecutor;

        appointmentDetailsContainer = findViewById(R.id.appointmentDetailsContainer);
        noAppointmentText = findViewById(R.id.noAppointmentText);
        medicationValue = findViewById(R.id.medicationValue);
        foodValue = findViewById(R.id.foodValue);
        dateValue = findViewById(R.id.dateValue);
        timeValue = findViewById(R.id.timeValue);
        cancelAppointmentButton = findViewById(R.id.cancelAppointmentButton);
        negativeButton = findViewById(R.id.negativeButton);

        cancelAppointmentButton.setOnClickListener(v -> {
            if (currentAppointment != null) {
                showCancelConfirmationDialog();
            }
        });
        negativeButton.setOnClickListener(v -> {
            showQuitConfirmationDialog();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAppointmentData();
    }

    private void loadAppointmentData() {
        databaseExecutor.execute(() -> {
            try {
                // We no longer need to check if appDb is null here because we initialize it in onCreate.
                currentAppointment = appDb.appointmentDAO().getActiveAppointmentForStudent(currentStudentId);

                if (currentAppointment != null) {
                    Student student = appDb.studentDAO().getStudentById(currentAppointment.getStuNum());
                    String foodRequirement = student != null ? student.getFoodReq() : "N/A";

                    String medicationRequirement = student != null ? student.getMedReq() : "N/A";
                    runOnUiThread(() -> {
                        appointmentDetailsContainer.setVisibility(View.VISIBLE);
                        noAppointmentText.setVisibility(View.GONE);
                        if (shouldShowCancelButton) {
                            cancelAppointmentButton.setVisibility(View.VISIBLE);
                        } else {
                            cancelAppointmentButton.setVisibility(View.GONE);
                        }

                        medicationValue.setText(TextUtils.isEmpty(medicationRequirement) ? "None" : medicationRequirement);                        dateValue.setText(currentAppointment.getDate());
                        timeValue.setText(currentAppointment.getTime());
                    });
                } else {
                    runOnUiThread(() -> {
                        appointmentDetailsContainer.setVisibility(View.GONE);
                        noAppointmentText.setVisibility(View.VISIBLE);
                        cancelAppointmentButton.setVisibility(View.GONE);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(StudentViewAppointmentActivity.this, "Error loading appointment data", Toast.LENGTH_SHORT).show();
                });
            }
        });
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

    private void showCancelConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.appnt_cancel_dialog, null);
        builder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);

        final AlertDialog dialog = builder.create();

        // FIX: Add this block to make the dialog background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.show();

        yesButton.setOnClickListener(v -> {
            databaseExecutor.execute(() -> {
                appDb.appointmentDAO().deleteAppointment(currentAppointment);
                appDb.appointmentMedicationDAO().deleteLinksForAppointment(currentAppointment.getAppointmentNum());

                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.appointment_cancelled_success), Toast.LENGTH_SHORT).show();
                    loadAppointmentData(); // Refresh the UI
                });
            });
            dialog.dismiss();
        });
        noButton.setOnClickListener(v -> dialog.dismiss());
    }
}