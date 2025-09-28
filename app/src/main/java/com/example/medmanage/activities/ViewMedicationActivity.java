package com.example.medmanage.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.medmanage.R;
import com.example.medmanage.model.Medication;
import com.example.medmanage.view_model.UserViewModel;

import java.util.List;

public class ViewMedicationActivity extends AppCompatActivity {

    private Spinner medicationSpinner, brandSpinner, dosageSpinner;
    private TextView quantityTextView;
    private Button addButton, updateButton, deleteButton;
    private View brandSpinnerContainer, dosageSpinnerContainer;

    private UserViewModel userViewModel;
    private LiveData<Medication> medicationDetailsLiveData;
    private Medication currentlySelectedMedication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_medication);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        medicationSpinner = findViewById(R.id.medicationSpinner);
        brandSpinner = findViewById(R.id.brandSpinner);
        dosageSpinner = findViewById(R.id.dosageSpinner);
        quantityTextView = findViewById(R.id.quantityTextView);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        brandSpinnerContainer = findViewById(R.id.brandSpinnerContainer);
        dosageSpinnerContainer = findViewById(R.id.dosageSpinnerContainer);

        brandSpinner.setEnabled(false);
        dosageSpinner.setEnabled(false);

        // 1. Load distinct medication names into the first spinner
        userViewModel.getAllMedications().observe(this, medications -> {
            if (medications != null) {
                // Extract distinct medication names
                // Note: This is a simplified approach. You might want to create a method in your DAO
                // to get distinct names directly from the database for better performance
                List<String> medNames = medications.stream()
                        .map(Medication::getMedName)
                        .distinct()
                        .collect(java.util.stream.Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, medNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                medicationSpinner.setAdapter(adapter);
            }
        });

        // 2. When a medication name is selected, clear child spinners and load brands
        medicationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) parent.getItemAtPosition(position);

                // Clear the child spinners and quantity text
                brandSpinner.setAdapter(null);
                dosageSpinner.setAdapter(null);
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;

                // Disable child spinners and set to dull until their data is loaded
                brandSpinner.setEnabled(false);
                brandSpinnerContainer.setAlpha(0.5f);
                dosageSpinner.setEnabled(false);
                dosageSpinnerContainer.setAlpha(0.5f);

                // Load brands for the selected medication
                userViewModel.getAllMedications().observe(ViewMedicationActivity.this, medications -> {
                    if (medications != null) {
                        List<String> brands = medications.stream()
                                .filter(med -> med.getMedName().equals(selectedName))
                                .map(Medication::getBrand)
                                .distinct()
                                .collect(java.util.stream.Collectors.toList());

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewMedicationActivity.this, R.layout.custom_spinner_item, brands);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        brandSpinner.setAdapter(adapter);

                        // Enable and brighten the spinner only if there's more than one choice
                        if (brands != null && brands.size() > 1) {
                            brandSpinner.setEnabled(true);
                            brandSpinnerContainer.setAlpha(1.0f);
                        }
                    }
                });
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                brandSpinner.setEnabled(false);
                brandSpinnerContainer.setAlpha(0.5f);
                dosageSpinner.setEnabled(false);
                dosageSpinnerContainer.setAlpha(0.5f);
            }
        });

        // 3. When a brand is selected, clear child spinner and load dosages
        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) medicationSpinner.getSelectedItem();
                String selectedBrand = (String) parent.getItemAtPosition(position);

                // Clear the child spinner and quantity text
                dosageSpinner.setAdapter(null);
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;

                // Disable the dosage spinner and set to dull until its data is loaded
                dosageSpinner.setEnabled(false);
                dosageSpinnerContainer.setAlpha(0.5f);

                // Load dosages for the selected medication and brand
                userViewModel.getAllMedications().observe(ViewMedicationActivity.this, medications -> {
                    if (medications != null) {
                        List<String> dosages = medications.stream()
                                .filter(med -> med.getMedName().equals(selectedName) && med.getBrand().equals(selectedBrand))
                                .map(Medication::getDosage)
                                .distinct()
                                .collect(java.util.stream.Collectors.toList());

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewMedicationActivity.this, R.layout.custom_spinner_item, dosages);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dosageSpinner.setAdapter(adapter);

                        // Enable and brighten the spinner only if there's more than one choice
                        if (dosages != null && dosages.size() > 1) {
                            dosageSpinner.setEnabled(true);
                            dosageSpinnerContainer.setAlpha(1.0f);
                        }
                    }
                });
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                dosageSpinner.setEnabled(false);
                dosageSpinnerContainer.setAlpha(0.5f);
            }
        });

        // 4. When a dosage is selected, get the full medication details
        dosageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) medicationSpinner.getSelectedItem();
                String selectedBrand = (String) brandSpinner.getSelectedItem();
                String selectedDosage = (String) parent.getItemAtPosition(position);

                if (selectedName == null || selectedBrand == null || selectedDosage == null) return;

                // Find the medication with the selected details
                userViewModel.getAllMedications().observe(ViewMedicationActivity.this, medications -> {
                    if (medications != null) {
                        Medication foundMedication = medications.stream()
                                .filter(med -> med.getMedName().equals(selectedName) &&
                                        med.getBrand().equals(selectedBrand) &&
                                        med.getDosage().equals(selectedDosage))
                                .findFirst()
                                .orElse(null);

                        if (foundMedication != null) {
                            currentlySelectedMedication = foundMedication;
                            quantityTextView.setText(getString(R.string.quantity_on_hand_label, foundMedication.getQuantityOnHand()));
                        }
                    }
                });
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;
            }
        });

        addButton.setOnClickListener(v -> showAddUpdateDialog(null));

        updateButton.setOnClickListener(v -> {
            if (currentlySelectedMedication != null) {
                showAddUpdateDialog(currentlySelectedMedication);
            } else {
                Toast.makeText(this, getString(R.string.select_med_to_update), Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (currentlySelectedMedication != null) {
                showDeleteConfirmationDialog(currentlySelectedMedication);
            } else {
                Toast.makeText(this, getString(R.string.select_med_to_delete), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddUpdateDialog(final Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_update_medication, null);
        builder.setView(dialogView);

        final EditText medNameEditText = dialogView.findViewById(R.id.medicationNameEditText);
        final EditText brandEditText = dialogView.findViewById(R.id.brandEditText);
        final EditText dosageEditText = dialogView.findViewById(R.id.dosageEditText);
        final EditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        final TextView titleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        final Button saveButton = dialogView.findViewById(R.id.saveButton);
        final Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        if (medication != null) {
            // Setup for "Update" dialog
            titleTextView.setText(getString(R.string.med_update));
            medNameEditText.setText(medication.getMedName());
            brandEditText.setText(medication.getBrand());
            dosageEditText.setText(medication.getDosage());
            quantityEditText.setText(String.valueOf(medication.getQuantityOnHand()));
        } else {
            // Setup for "Add" dialog
            titleTextView.setText(getString(R.string.med_add));
        }

        final AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(view -> {
            String medName = medNameEditText.getText().toString().trim();
            String brand = brandEditText.getText().toString().trim();
            String dosage = dosageEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();

            if (medName.isEmpty() || brand.isEmpty() || dosage.isEmpty() || quantityStr.isEmpty()){
                Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!dosage.toLowerCase().contains("mg")) {
                Toast.makeText(this, getString(R.string.error_dosage_format), Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.error_invalid_number), Toast.LENGTH_SHORT).show();
                return;
            }

            if (quantity < 0) {
                Toast.makeText(this, getString(R.string.error_negative_quantity), Toast.LENGTH_SHORT).show();
                return;
            }

            if (medication != null) {
                // Update existing medication
                medication.setMedName(medName);
                medication.setBrand(brand);
                medication.setDosage(dosage);
                medication.setQuantityOnHand(quantity);
                userViewModel.updateMedication(medication);
                Toast.makeText(this, getString(R.string.med_update_message), Toast.LENGTH_SHORT).show();
            } else {
                // Add new medication
                Medication newMedication = new Medication(medName, brand, dosage, quantity);
                userViewModel.insertMedication(newMedication);
                Toast.makeText(this, getString(R.string.med_add_message), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmationDialog(final Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirmation_medication, null);
        builder.setView(dialogView);

        final TextView messageTextView = dialogView.findViewById(R.id.confirmationMessageTextView);
        final Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        final Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        messageTextView.setText(getString(R.string.confirm_delete_med, medication.getMedName()));

        final AlertDialog dialog = builder.create();

        positiveButton.setOnClickListener(v -> {
            userViewModel.deleteMedication(medication);
            Toast.makeText(this, getString(R.string.med_deleted_success, medication.getMedName()), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}