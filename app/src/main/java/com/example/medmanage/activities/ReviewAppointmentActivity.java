package com.example.medmanage.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.AppointmentWithStudent;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ReviewAppointmentActivity extends AppCompatActivity {

    private ConstraintLayout customSpinner;
    private TextView selectedItemTextView;
    private TextView nameTextView;
    private TextView typeTextView;
    private TextView quantityTextView;
    private ProgressBar progressBar;
    private TextView loadingText;
    private TextView errorText;
    private TextView header;
    private MaterialCardView detailsCard;
    private Button editButton;

    private databaseMedicManage appDb;
    private LiveData<List<AppointmentWithStudent>> appointmentsLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appnt_review);

        initializeViews();

        appDb = databaseMedicManage.getDatabase(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAppointmentData();
    }

    private void initializeViews() {
        customSpinner = findViewById(R.id.customSpinner);
        selectedItemTextView = findViewById(R.id.selectedItemTextView);
        nameTextView = findViewById(R.id.nameTextView);
        typeTextView = findViewById(R.id.typeTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        errorText = findViewById(R.id.errorText);
        header = findViewById(R.id.header);
        detailsCard = findViewById(R.id.detailsCard);
        editButton = findViewById(R.id.editButton);

        editButton.setVisibility(View.GONE);
    }

    private void fetchAppointmentData() {
        showLoading(true);
        if (appDb == null) {
            showError();
            return;
        }

        appointmentsLiveData = appDb.appointmentDAO().getAllAppointmentsWithStudents();
        appointmentsLiveData.observe(this, appointmentsWithStudents -> {
            if (appointmentsWithStudents == null || appointmentsWithStudents.isEmpty()) {
                selectedItemTextView.setText(R.string.no_appointments_found);
                showLoading(false);
                updateDetails(null);
            } else {
                setupCustomSpinner(appointmentsWithStudents);
                showLoading(false);
            }
        });
    }

    private void setupCustomSpinner(List<AppointmentWithStudent> appointmentList) {
        if (isFinishing() || isDestroyed() || appointmentList == null || appointmentList.isEmpty()) {
            return;
        }

        AppointmentWithStudent selectedAppointmentWithStudent = appointmentList.get(0);
        updateDetails(selectedAppointmentWithStudent);
        selectedItemTextView.setText(String.format("ID: %s | Date: %s | Time: %s",
                selectedAppointmentWithStudent.student.getStuNum(),
                selectedAppointmentWithStudent.appointment.getDate(),
                selectedAppointmentWithStudent.appointment.getTime()));

        customSpinner.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(ReviewAppointmentActivity.this, v);
            for (AppointmentWithStudent item : appointmentList) {
                String itemText = String.format("ID: %s | Date: %s | Time: %s",
                        item.student.getStuNum(),
                        item.appointment.getDate(),
                        item.appointment.getTime());
                popupMenu.getMenu().add(itemText);
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedTitle = item.getTitle().toString();
                for (AppointmentWithStudent currentItem : appointmentList) {
                    String itemText = String.format("ID: %s | Date: %s | Time: %s",
                            currentItem.student.getStuNum(),
                            currentItem.appointment.getDate(),
                            currentItem.appointment.getTime());
                    if (itemText.equals(selectedTitle)) {
                        updateDetails(currentItem);
                        selectedItemTextView.setText(selectedTitle);
                        break;
                    }
                }
                return true;
            });

            popupMenu.show();
        });
    }

    // Removed the StringFormatInvalid lint suppression as it's no longer needed
    private void updateDetails(AppointmentWithStudent appointmentWithStudent) {
        if (appointmentWithStudent != null) {
            nameTextView.setText("Student Number: " + appointmentWithStudent.student.getStuNum());
            typeTextView.setText("Medication Requirement: " + appointmentWithStudent.student.getMedReq());
            quantityTextView.setText("Appointment Date: " + appointmentWithStudent.appointment.getDate());
        } else {
            nameTextView.setText(R.string.not_available);
            typeTextView.setText(R.string.not_available);
            quantityTextView.setText(R.string.not_available);
        }
    }

    private void showLoading(boolean isLoading) {
        int contentVisibility = isLoading ? View.GONE : View.VISIBLE;
        int loadingVisibility = isLoading ? View.VISIBLE : View.GONE;

        progressBar.setVisibility(loadingVisibility);
        loadingText.setVisibility(loadingVisibility);

        header.setVisibility(contentVisibility);
        customSpinner.setVisibility(contentVisibility);
        detailsCard.setVisibility(contentVisibility);
        editButton.setVisibility(contentVisibility);

        errorText.setVisibility(View.GONE);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);

        header.setVisibility(View.GONE);
        customSpinner.setVisibility(View.GONE);
        detailsCard.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        errorText.setVisibility(View.VISIBLE);
    }
}
