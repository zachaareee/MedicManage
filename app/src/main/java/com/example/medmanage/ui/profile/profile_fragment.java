package com.example.medmanage.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // <-- ADD THIS IMPORT
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.medmanage.R;
import com.example.medmanage.activities.SigninActivity;
import com.example.medmanage.activities.UpdateProfileActivity;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

public class profile_fragment extends Fragment {

    private static final String TAG = "ProfileFragment"; // Tag for logging

    private static final String ARG_USERNAME = "USERNAME";
    private static final String ARG_USER_TYPE = "USER_TYPE";

    private TextView profileName, profileNumber,profileSurname, profileUsername,
            medicationRequirement, foodRequirement, profilePassword;
    private Button updateButton, deleteButton;
    private databaseMedicManage db;
    private Object currentUser;
    private String username;
    private String userType;

    private ActivityResultLauncher<Intent> updateProfileLauncher;

    public profile_fragment() {
        // Required empty public constructor
    }

    public static profile_fragment newInstance(String username, String userType) {
        profile_fragment fragment = new profile_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, username);
        args.putString(ARG_USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_USERNAME")) {
                            username = data.getStringExtra("UPDATED_USERNAME");
                        }
                        fetchUserData(username, userType);
                        Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = databaseMedicManage.getDatabase(getContext());
        initializeViews(view);

        if (getArguments() != null) {
            username = getArguments().getString(ARG_USERNAME);
            userType = getArguments().getString(ARG_USER_TYPE);
        }

        // --- DEBUG STEP 1: Check if arguments are received ---
        Log.d(TAG, "Arguments received: Username=" + username + ", UserType=" + userType);

        updateButton.setOnClickListener(v -> openUpdateActivity());
        deleteButton.setOnClickListener(v -> deleteUser());

        if (username != null && userType != null) {
            fetchUserData(username, userType);
        } else {
            // --- DEBUG STEP 1: Log an error if arguments are null ---
            Log.e(TAG, "Username or UserType is null. Cannot fetch data.");
        }
    }

    private void fetchUserData(String username, String userType) {
        Log.d(TAG, "Fetching data for user: " + username);
        if ("student".equalsIgnoreCase(userType)) {
            db.studentDAO().getStudentByUsernameLive(username).observe(getViewLifecycleOwner(), student -> {
                // --- DEBUG STEP 2: Check the result from the database ---
                Log.d(TAG, "Student query returned: " + (student == null ? "null" : student.toString()));
                if (student != null) {
                    currentUser = student;
                    populateUI();
                }
            });
        } else if ("nurse".equalsIgnoreCase(userType)) {
            db.nurseDAO().getNurseByUsernameLive(username).observe(getViewLifecycleOwner(), nurse -> {
                // --- DEBUG STEP 2: Check the result from the database ---
                Log.d(TAG, "Nurse query returned: " + (nurse == null ? "null" : nurse.toString()));
                if (nurse != null) {
                    currentUser = nurse;
                    populateUI();
                }
            });
        }
    }

    private void initializeViews(View view) {
        // Use the passed-in view to find the UI elements
        profileName = view.findViewById(R.id.textView_firstName);
        profileSurname = view.findViewById(R.id.textView_lastName);
        profileNumber = view.findViewById(R.id.textView_studentNo);
        profileUsername = view.findViewById(R.id.textView_username);
        medicationRequirement = view.findViewById(R.id.textView_medicationReq);
        foodRequirement = view.findViewById(R.id.textView_foodReq);
        profilePassword = view.findViewById(R.id.textView_password);
        updateButton = view.findViewById(R.id.button_update);
        deleteButton = view.findViewById(R.id.button_delete);
    }

    private void populateUI() {
        // This method is identical to the one in your original Activity
        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            profileName.setText("Name\n" + student.getStuName());
            profileSurname.setText("Surname\n" + student.getStuSurname());
            profileUsername.setText("Username\n" + student.getUserName());
            profileNumber.setText("Student Number\n" + student.getStuNum());
            medicationRequirement.setText("Medication Requirement\n" + student.getMedReq());
            foodRequirement.setText("Food Requirement\n" + student.getFoodReq());
            profilePassword.setText("Password\n********");

            profileNumber.setVisibility(View.VISIBLE);
            medicationRequirement.setVisibility(View.VISIBLE);
            foodRequirement.setVisibility(View.VISIBLE);

        } else if (currentUser instanceof Nurse) {
            Nurse nurse = (Nurse) currentUser;
            profileName.setText("Name\n" + nurse.getEmpName());
            profileSurname.setText("Surname\n" + nurse.getEmpSurname());
            profileUsername.setText("Username\n" + nurse.getEmpUserName());
            profileNumber.setText("Staff Number\n" + nurse.getEmpNum());
            profilePassword.setText("Password\n********");

            // Ensure these are hidden for the nurse profile
            profileNumber.setVisibility(View.VISIBLE); // Assuming staff number should be visible
            medicationRequirement.setVisibility(View.GONE);
            foodRequirement.setVisibility(View.GONE);
        }
    }

    private void openUpdateActivity() {
        if (currentUser == null) return;

        // Use getContext() to create intents from a Fragment
        Intent intent = new Intent(getContext(), UpdateProfileActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("USER_TYPE", userType);
        updateProfileLauncher.launch(intent);
    }

    private void deleteUser() {
        // Use getContext() or requireContext() for the AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.profile_delete_dialog, null);
        builder.setView(dialogView);

        Button negativeButton = dialogView.findViewById(R.id.negativeButton);
        Button positiveButton = dialogView.findViewById(R.id.positiveButton);

        AlertDialog dialog = builder.create();
        dialog.show();

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
            // Database operation runs in the background
            if (currentUser instanceof Student) {
                db.studentDAO().deleteStudent((Student) currentUser);
            } else if (currentUser instanceof Nurse) {
                db.nurseDAO().deleteNurse((Nurse) currentUser);
            }

            Toast.makeText(getContext(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), SigninActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}