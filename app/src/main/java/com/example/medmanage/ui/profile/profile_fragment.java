package com.example.medmanage.ui.profile;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.example.medmanage.R;
import com.example.medmanage.activities.SigninActivity;
import com.example.medmanage.activities.UpdateProfileActivity;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

public class profile_fragment extends Fragment {

    // UI Elements
    private TextView textViewSignOut, textViewFirstName, textViewLastName, textViewUsername;
    private TextView labelUserNumber, textViewUserNumber; // Dynamic field for Student/Employer No
    private TextView textViewMedicationReq, textViewFoodReq;
    private Group studentFieldsGroup; // Group to hide/show student-only fields
    private Button buttonUpdate, buttonDelete;

    // Data
    private databaseMedicManage db;
    private String username;
    private String userType;
    private Object currentUser;

    // Activity Result Launcher
    private ActivityResultLauncher<Intent> updateProfileLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the launcher to handle the result from UpdateProfileActivity
        updateProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_USERNAME")) {
                            this.username = data.getStringExtra("UPDATED_USERNAME");
                        }
                        // Refresh the profile data on screen after an update
                        loadUserProfile();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = databaseMedicManage.getDatabase(requireContext());

        if (getActivity() != null && getActivity().getIntent() != null) {
            username = getActivity().getIntent().getStringExtra("USERNAME");
            userType = getActivity().getIntent().getStringExtra("USER_TYPE");
        }

        initializeViews(view);
        setupListeners();
        loadUserProfile();
    }

    private void initializeViews(View view) {
        // Common fields
        textViewSignOut = view.findViewById(R.id.textView_signOut);
        textViewFirstName = view.findViewById(R.id.textView_firstName);
        textViewLastName = view.findViewById(R.id.textView_lastName);
        textViewUsername = view.findViewById(R.id.textView_username);

        // Dynamic user number field
        labelUserNumber = view.findViewById(R.id.label_userNumber);
        textViewUserNumber = view.findViewById(R.id.textView_userNumber);

        // Student-only fields
        studentFieldsGroup = view.findViewById(R.id.group_student_fields);
        textViewMedicationReq = view.findViewById(R.id.textView_medicationReq);
        textViewFoodReq = view.findViewById(R.id.textView_foodReq);

        // Buttons
        buttonUpdate = view.findViewById(R.id.button_update);
        buttonDelete = view.findViewById(R.id.button_delete);
    }

    private void setupListeners() {
        buttonUpdate.setOnClickListener(v -> openUpdateActivity());
        buttonDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
        textViewSignOut.setOnClickListener(v -> showSignOutConfirmationDialog());
    }

    private void loadUserProfile() {
        if (username == null || userType == null) return;

        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            if ("student".equalsIgnoreCase(userType)) {
                Student student = db.studentDAO().getStudentByUsername(username);
                if (student != null) {
                    currentUser = student;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> populateUI(student));
                    }
                }
            } else if ("nurse".equalsIgnoreCase(userType)) {
                Nurse nurse = db.nurseDAO().getNurseByUsername(username);
                if (nurse != null) {
                    currentUser = nurse;
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> populateUI(nurse));
                    }
                }
            }
        });
    }

    // Overloaded method to populate UI for a Student
    private void populateUI(Student student) {
        // Show student-specific fields
        studentFieldsGroup.setVisibility(View.VISIBLE);

        // Set common fields
        textViewFirstName.setText(student.getStuName());
        textViewLastName.setText(student.getStuSurname());
        textViewUsername.setText(student.getUserName());

        // Set dynamic label and value for Student Number
        labelUserNumber.setText(R.string.studentno);
        textViewUserNumber.setText(String.valueOf(student.getStuNum()));

        // Set student-specific fields
        textViewMedicationReq.setText(student.getMedReq());
        textViewFoodReq.setText(student.getFoodReq());
    }

    // Overloaded method to populate UI for a Nurse
    private void populateUI(Nurse nurse) {
        // Hide student-specific fields
        studentFieldsGroup.setVisibility(View.GONE);

        // Set common fields
        textViewFirstName.setText(nurse.getEmpName());
        textViewLastName.setText(nurse.getEmpSurname());
        textViewUsername.setText(nurse.getEmpUserName());

        // Set dynamic label and value for Employer Number
        labelUserNumber.setText(R.string.employee_no);
        textViewUserNumber.setText(String.valueOf(nurse.getEmpNum()));
    }

    private void openUpdateActivity() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User data not loaded yet.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(requireActivity(), UpdateProfileActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("USER_TYPE", userType);
        updateProfileLauncher.launch(intent);
    }

    // Other methods (dialogs) remain the same
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to permanently delete your account?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUserFromDatabase())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUserFromDatabase() {
        databaseMedicManage.databaseWriteExecutor.execute(() -> {
            if (currentUser instanceof Student) {
                db.studentDAO().deleteStudent((Student) currentUser);
            } else if (currentUser instanceof Nurse) {
                db.nurseDAO().deleteNurse((Nurse) currentUser);
            }
            if(getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Account deleted successfully.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getContext(), SigninActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                });
            }
        });
    }

    // In profile_fragment.java

// ... (other methods)

    private void showSignOutConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_out_dialog, null);
        builder.setView(dialogView);

        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        final androidx.appcompat.app.AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (getContext() != null) {
                Intent intent = new Intent(getContext(), SigninActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

}


