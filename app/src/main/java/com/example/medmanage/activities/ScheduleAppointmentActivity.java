// File: ScheduleAppointmentActivity.java

package com.example.medmanage.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medmanage.R;
import com.example.medmanage.adapters.DateAdapter;
import com.example.medmanage.adapters.TimeAdapter;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Appointment;
import com.example.medmanage.model.Appointment_Medication;
import com.example.medmanage.model.Medication;
import com.example.medmanage.model.Student;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class ScheduleAppointmentActivity extends AppCompatActivity {

    public static final String STUDENT_ID_EXTRA = "student_id_extra";
    private RecyclerView dateRecyclerView, timeSlotsRecyclerView;
    private Button bookButton, quitButton;
    private DateAdapter dateAdapter;
    private TimeAdapter timeAdapter;
    private Date selectedDate = null;
    private String selectedTime = null;
    private int currentStudentId;
    private databaseMedicManage appDb;
    private ExecutorService databaseExecutor;
    private final SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_appointment);

        // REMOVED FOR TESTING, UNCOMMENTED FOR PRODUCTION:
        currentStudentId = getIntent().getIntExtra(STUDENT_ID_EXTRA, -1);
        if (currentStudentId == -1) {
            Toast.makeText(this, "Error: User not identified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        appDb= databaseMedicManage.getDatabase(getApplicationContext(), new databaseMedicManage.DatabaseCallback() {
            @Override
            public void onDatabaseReady(databaseMedicManage database) {
                appDb = database;
            }
        });

        dateRecyclerView = findViewById(R.id.dateRecyclerView);
        timeSlotsRecyclerView = findViewById(R.id.timeSlotsRecyclerView);
        bookButton = findViewById(R.id.bookButton);
        quitButton = findViewById(R.id.quitButton);
        TextView monthTextView = findViewById(R.id.monthTextView);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthTextView.setText(monthFormat.format(new Date()));

        setupDateRecyclerView();
        setupTimeRecyclerView();
        setTimeSlotsActive(false);

        bookButton.setOnClickListener(v -> bookAppointment());
        quitButton.setOnClickListener(v -> showQuitConfirmationDialog());
    }

    private void setupDateRecyclerView() {
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Date> dates = generateNextSevenWeekdays();
        dateAdapter = new DateAdapter(this, dates, date -> {
            selectedDate = date;
            selectedTime = null;
            if (date != null) {
                fetchBookedSlotsForDate(date);
                setTimeSlotsActive(true);
            } else {
                timeAdapter.setBookedTimes(new ArrayList<>());
                setTimeSlotsActive(false);
            }
        });
        dateRecyclerView.setAdapter(dateAdapter);
    }

    private void setupTimeRecyclerView() {
        timeSlotsRecyclerView.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 3));
        List<String> times = generateTimeSlots();
        timeAdapter = new TimeAdapter(this, times, time -> selectedTime = time);
        timeSlotsRecyclerView.setAdapter(timeAdapter);
    }

    private void setTimeSlotsActive(boolean isActive) {
        timeSlotsRecyclerView.setAlpha(isActive ? 1.0f : 0.4f);
    }

    private void fetchBookedSlotsForDate(Date date) {
        String dateToQuery = dbFormat.format(date);
        appDb.appointmentDAO().getAppointmentsByDate(dateToQuery).observe(this, appointmentsOnDate -> {
            if (appointmentsOnDate != null) {
                List<String> bookedTimes = appointmentsOnDate.stream()
                        .map(Appointment::getTime)
                        .collect(Collectors.toList());
                timeAdapter.setBookedTimes(bookedTimes);
            }
        });
    }

    private void bookAppointment() {
        if (selectedDate == null || selectedTime == null) {
            Toast.makeText(this, R.string.select_date_and_time, Toast.LENGTH_SHORT).show();
            return;
        }

        final String dateToStore = dbFormat.format(selectedDate);
        final String todayDate = dbFormat.format(new Date());

        databaseExecutor.execute(() -> {
            Appointment activeAppointment = appDb.appointmentDAO().getActiveAppointmentForStudent(currentStudentId);
            if (activeAppointment != null) {
                runOnUiThread(() -> Toast.makeText(this, R.string.error_existing_booking, Toast.LENGTH_LONG).show());
                return;
            }

            Appointment existingSlot = appDb.appointmentDAO().getAppointmentByDateTime(dateToStore, selectedTime);
            if (existingSlot != null) {
                runOnUiThread(() -> Toast.makeText(this, R.string.slot_unavailable, Toast.LENGTH_SHORT).show());
                return;
            }

            final int nurseId = 1001;
            final int foodId = 1;

            Appointment newAppointment = new Appointment(currentStudentId, nurseId, foodId, dateToStore, selectedTime);
            appDb.appointmentDAO().insertAppointment(newAppointment);

            // Get the appointment we just created to link its medication
            Appointment insertedAppointment = appDb.appointmentDAO().getAppointmentByDateTime(dateToStore, selectedTime);
            if (insertedAppointment != null) {
                Student student = appDb.studentDAO().getStudentById(currentStudentId);
                if (student != null && student.getMedReq() != null && !student.getMedReq().isEmpty()) {
                    Medication requiredMed = appDb.medicationDAO().getMedicationByName("%" + student.getMedReq().trim() + "%");
                    if (requiredMed != null) {
                        Appointment_Medication newLink = new Appointment_Medication(insertedAppointment.getAppointmentNum(), requiredMed.getMedID());
                        appDb.appointmentMedicationDAO().insert(newLink);
                    }
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, R.string.appointment_booked_success, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_quit_schedule, null);
        builder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);

        final AlertDialog dialog = builder.create();
        dialog.show();

        yesButton.setOnClickListener(v -> finish());
        noButton.setOnClickListener(v -> dialog.dismiss());
    }

    private List<String> generateTimeSlots() {
        List<String> timeSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(Calendar.HOUR_OF_DAY, 15);
        endCalendar.set(Calendar.MINUTE, 30);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        while (calendar.before(endCalendar) || calendar.equals(endCalendar)) {
            timeSlots.add(timeFormat.format(calendar.getTime()));
            calendar.add(Calendar.MINUTE, 30);
        }
        return timeSlots;
    }

    private List<Date> generateNextSevenWeekdays() {
        List<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        if (calendar.get(Calendar.HOUR_OF_DAY) >= 16) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        while (dateList.size() < 7) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                dateList.add(calendar.getTime());
            }
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dateList;
    }
}