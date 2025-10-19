package com.example.medmanage.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.medmanage.R;
import com.example.medmanage.activities.ReviewAppointmentActivity;
import com.example.medmanage.activities.ScheduleAppointmentActivity;
import com.example.medmanage.activities.ViewAppointmentActivity;

public class schedule_fragment extends Fragment {
    private String userType;
    private int studentId = -1;

    public schedule_fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get user data once when the fragment is created
        if (getActivity() != null && getActivity().getIntent() != null) {
            userType = getActivity().getIntent().getStringExtra("USER_TYPE");
            studentId = getActivity().getIntent().getIntExtra("STUDENT_ID", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (userType != null && userType.equals("nurse")) {
            // User is a NURSE, so inflate the nurse layout
            view = inflater.inflate(R.layout.fragment_schedule_nurse, container, false);
        } else {
            view = inflater.inflate(R.layout.fragment_schedule_student, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (userType != null && userType.equals("nurse")) {
            setupNurseClickListeners(view);
        } else {
            setupStudentClickListeners(view, studentId);
        }
    }

    private void setupStudentClickListeners(View studentView, int finalStudentId) {
        CardView scheduleAppointmentCard = studentView.findViewById(R.id.schedule);
        CardView cancelAppointmentCard = studentView.findViewById(R.id.cancel);
        CardView viewAppointmentCard = studentView.findViewById(R.id.view_appnt);

        scheduleAppointmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScheduleAppointmentActivity.class);
            intent.putExtra(ScheduleAppointmentActivity.STUDENT_ID_EXTRA, finalStudentId);
            startActivity(intent);
        });

        cancelAppointmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewAppointmentActivity.class);
            intent.putExtra(ViewAppointmentActivity.STUDENT_ID_EXTRA, finalStudentId);
            intent.putExtra(ViewAppointmentActivity.SHOW_CANCEL_BUTTON_EXTRA, true);
            startActivity(intent);
        });

        viewAppointmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewAppointmentActivity.class);
            intent.putExtra(ViewAppointmentActivity.STUDENT_ID_EXTRA, finalStudentId);
            startActivity(intent);
        });

    }

    private void setupNurseClickListeners(View nurseView) {
        CardView viewAppointmentsCard = nurseView.findViewById(R.id.view_Appnt);
        CardView reviewAppointmentsCard = nurseView.findViewById(R.id.view_food_list);

        viewAppointmentsCard.setOnClickListener(v -> {

             Intent intent = new Intent(getActivity(), ReviewAppointmentActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "Nurse: View All Appointments Clicked", Toast.LENGTH_SHORT).show();
        });

        reviewAppointmentsCard.setOnClickListener(v -> {

             Intent intent = new Intent(getActivity(), ReviewAppointmentActivity.class);
             startActivity(intent);
            Toast.makeText(getContext(), "Nurse: Review Appointments Clicked", Toast.LENGTH_SHORT).show();
        });
    }
}

