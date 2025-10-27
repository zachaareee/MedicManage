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
import androidx.fragment.app.Fragment;

import com.example.medmanage.R;
import com.example.medmanage.activities.SigninActivity;
import com.example.medmanage.activities.UpdateProfileActivity;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Nurse;
import com.example.medmanage.model.Student;

public class profile_fragment extends Fragment {

    private TextView textViewSignOut, textViewFirstName, textViewLastName, textViewUsername;
    private TextView labelUserNumber, textViewUserNumber;
    private TextView textViewMedicationReq, textViewFoodReq, labelMedicationReq, labelFoodReq;
    private Button buttonUpdate, buttonDelete;

    private databaseMedicManage db;
    private String username;
    private String userType;
    private Object currentUser;

    private ActivityResultLauncher<Intent> updateProfileLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_USERNAME")) {
                            this.username = data.getStringExtra("UPDATED_USERNAME");
                        }
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
        textViewSignOut = view.findViewById(R.id.textView_signOut);
        textViewFirstName = view.findViewById(R.id.textView_firstName);
        textViewLastName = view.findViewById(R.id.textView_lastName);
        textViewUsername = view.findViewById(R.id.textView_username);

        labelUserNumber = view.findViewById(R.id.label_userNumber);
        textViewUserNumber = view.findViewById(R.id.textView_userNumber);

        labelMedicationReq = view.findViewById(R.id.label_medicationReq);
        textViewMedicationReq = view.findViewById(R.id.textView_medicationReq);
        labelFoodReq = view.findViewById(R.id.label_foodReq);
        textViewFoodReq = view.findViewById(R.id.textView_foodReq);

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

    private void populateUI(Student student) {
        labelMedicationReq.setVisibility(View.VISIBLE);
        textViewMedicationReq.setVisibility(View.VISIBLE);
        labelFoodReq.setVisibility(View.VISIBLE);
        textViewFoodReq.setVisibility(View.VISIBLE);

        textViewFirstName.setText(student.getStuName());
        textViewLastName.setText(student.getStuSurname());
        textViewUsername.setText(student.getUserName());

        labelUserNumber.setText(R.string.studentno);
        textViewUserNumber.setText(String.valueOf(student.getStuNum()));

        textViewMedicationReq.setText(student.getMedReq());
        textViewFoodReq.setText(student.getFoodReq());
    }

    private void populateUI(Nurse nurse) {
        labelMedicationReq.setVisibility(View.GONE);
        textViewMedicationReq.setVisibility(View.GONE);
        labelFoodReq.setVisibility(View.GONE);
        textViewFoodReq.setVisibility(View.GONE);

        textViewFirstName.setText(nurse.getEmpName());
        textViewLastName.setText(nurse.getEmpSurname());
        textViewUsername.setText(nurse.getEmpUserName());

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

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.profile_delete_dialog, null);
        builder.setView(dialogView);

        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        final AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        positiveButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteUserFromDatabase();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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

    private void showSignOutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_out_dialog, null);
        builder.setView(dialogView);

        Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        final AlertDialog dialog = builder.create();

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