package com.example.medmanage.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.medmanage.R;
import com.example.medmanage.model.Medication;
import com.example.medmanage.processor.StringProcessor;
import com.example.medmanage.view_model.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.stream.Collectors;

public class ViewMedicationActivity extends AppCompatActivity {

    private Spinner medicationSpinner, brandSpinner, dosageSpinner;
    private TextView quantityTextView;
    private Button addButton, updateButton, deleteButton, quitButton;
    private ImageView brandDropdownArrow, dosageDropdownArrow;
    private UserViewModel userViewModel;
    private Medication currentlySelectedMedication;
    private int currentQuantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.med_view);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        medicationSpinner = findViewById(R.id.medicationSpinner);
        brandSpinner = findViewById(R.id.brandSpinner);
        dosageSpinner = findViewById(R.id.dosageSpinner);
        quantityTextView = findViewById(R.id.quantityTextView);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        quitButton = findViewById(R.id.quitButton);
        brandDropdownArrow = findViewById(R.id.brandDropdownArrow);
        dosageDropdownArrow = findViewById(R.id.dosageDropdownArrow);

        String userType = getIntent().getStringExtra("USER_TYPE");
        if (userType != null && userType.equals("student")) {
            addButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }

        setupArrowClickListeners();
        setupSpinnerTouchListeners();
        resetBrandSpinner();
        resetDosageSpinner();

        loadMedicationNames();
        setupSpinnerListeners();

        addButton.setOnClickListener(v -> showAddDialog());

        updateButton.setOnClickListener(v -> {
            if (currentlySelectedMedication != null) {
                showUpdateDialog(currentlySelectedMedication);
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

        quitButton.setOnClickListener(v -> showQuitConfirmationDialog());
    }

    private void setupArrowClickListeners() {
        brandDropdownArrow.setOnClickListener(v -> {
            if (brandSpinner.getAdapter() != null && brandSpinner.getAdapter().getCount() > 1) {
                brandSpinner.performClick();
            }
        });

        dosageDropdownArrow.setOnClickListener(v -> {
            if (dosageSpinner.getAdapter() != null && dosageSpinner.getAdapter().getCount() > 1) {
                dosageSpinner.performClick();
            }
        });
    }

    private void setupSpinnerTouchListeners() {
        brandSpinner.setOnTouchListener((v, event) -> {
            if (brandSpinner.getAdapter() != null && brandSpinner.getAdapter().getCount() <= 1) {
                return true;
            }
            return false;
        });

        dosageSpinner.setOnTouchListener((v, event) -> {
            if (dosageSpinner.getAdapter() != null && dosageSpinner.getAdapter().getCount() <= 1) {
                return true;
            }
            return false;
        });
    }

    private void loadMedicationNames() {
        userViewModel.getAllMedications().observe(this, medications -> {
            if (medications != null) {
                List<String> medNames = medications.stream()
                        .map(Medication::getMedName)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.appnt_spinner_item, medNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                medicationSpinner.setAdapter(adapter);

                resetBrandSpinner();
                resetDosageSpinner();
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;
            }
        });
    }

    private void setupSpinnerListeners() {
        medicationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) parent.getItemAtPosition(position);
                resetBrandSpinner();
                resetDosageSpinner();
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;

                userViewModel.getAllMedications().observe(ViewMedicationActivity.this, medications -> {
                    if (medications != null) {
                        List<String> brands = medications.stream()
                                .filter(med -> med.getMedName().equals(selectedName))
                                .map(Medication::getBrand)
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewMedicationActivity.this, R.layout.appnt_spinner_item, brands);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        brandSpinner.setAdapter(adapter);

                        if (brands.size() <= 1) {
                            brandDropdownArrow.setAlpha(0.5f);
                        } else {
                            brandDropdownArrow.setAlpha(1.0f);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resetBrandSpinner();
                resetDosageSpinner();
            }
        });

        brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) medicationSpinner.getSelectedItem();
                String selectedBrand = (String) parent.getItemAtPosition(position);

                resetDosageSpinner();
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;

                if (selectedName == null || selectedBrand == null) return;

                userViewModel.getAllMedications().observe(ViewMedicationActivity.this, medications -> {
                    if (medications != null) {
                        List<String> dosages = medications.stream()
                                .filter(med -> med.getMedName().equals(selectedName) && med.getBrand().equals(selectedBrand))
                                .map(Medication::getDosage)
                                .distinct()
                                .sorted()
                                .collect(Collectors.toList());

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewMedicationActivity.this, R.layout.appnt_spinner_item, dosages);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dosageSpinner.setAdapter(adapter);

                        if (dosages.size() <= 1) {
                            dosageDropdownArrow.setAlpha(0.5f);
                        } else {
                            dosageDropdownArrow.setAlpha(1.0f);
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                resetDosageSpinner();
            }
        });

        dosageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = (String) medicationSpinner.getSelectedItem();
                String selectedBrand = (String) brandSpinner.getSelectedItem();
                String selectedDosage = (String) parent.getItemAtPosition(position);

                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;

                if (selectedName == null || selectedBrand == null || selectedDosage == null) return;

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
                        } else {
                            quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                            currentlySelectedMedication = null;
                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                quantityTextView.setText(getString(R.string.quantity_on_hand_empty));
                currentlySelectedMedication = null;
            }
        });
    }

    private void resetBrandSpinner() {
        brandSpinner.setAdapter(null);
        brandDropdownArrow.setAlpha(0.5f);
    }

    private void resetDosageSpinner() {
        dosageSpinner.setAdapter(null);
        dosageDropdownArrow.setAlpha(0.5f);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.med_add_dialog, null);
        builder.setView(dialogView);

        final TextInputEditText medNameEditText = dialogView.findViewById(R.id.medicationNameEditText);
        final TextInputEditText brandEditText = dialogView.findViewById(R.id.brandEditText);
        final TextInputEditText dosageEditText = dialogView.findViewById(R.id.dosageEditText);
        final TextInputEditText quantityEditText = dialogView.findViewById(R.id.quantityEditText);
        final Button saveButton = dialogView.findViewById(R.id.saveButton);
        final Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        saveButton.setOnClickListener(view -> {
            String medNameInput = medNameEditText.getText().toString();
            String brandInput = brandEditText.getText().toString();
            String dosageInput = dosageEditText.getText().toString();
            String quantityStr = quantityEditText.getText().toString().trim();

            if (TextUtils.isEmpty(medNameInput) || TextUtils.isEmpty(brandInput) || TextUtils.isEmpty(dosageInput) || TextUtils.isEmpty(quantityStr)) {
                Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!StringProcessor.isValidName(medNameInput)) {
                Toast.makeText(this, getString(R.string.error_invalid_med_name), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!StringProcessor.isValidName(brandInput)) {
                Toast.makeText(this, getString(R.string.error_invalid_brand_name), Toast.LENGTH_SHORT).show();
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

            if (quantity > 10000) {
                Toast.makeText(this, getString(R.string.error_quantity_too_large), Toast.LENGTH_SHORT).show();
                return;
            }

            double dosageValue = StringProcessor.parseDosageValue(dosageInput);
            if (dosageValue <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_dosage_value), Toast.LENGTH_SHORT).show();
                return;
            }
            if (dosageValue > 5000) {
                Toast.makeText(this, getString(R.string.error_dosage_too_large), Toast.LENGTH_SHORT).show();
                return;
            }

            String medName = StringProcessor.toTitleCase(medNameInput);
            String brand = StringProcessor.toTitleCase(brandInput);
            String dosage = StringProcessor.formatDosage(dosageInput);

            Medication newMedication = new Medication(medName, brand, dosage, quantity);
            showSaveConfirmationDialog(newMedication);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void showUpdateDialog(final Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.med_update_dialog, null);
        builder.setView(dialogView);

        final TextInputEditText medNameEditText = dialogView.findViewById(R.id.medicationNameEditText);
        final TextInputEditText brandEditText = dialogView.findViewById(R.id.brandEditText);
        final TextInputEditText dosageEditText = dialogView.findViewById(R.id.dosageEditText);
        final TextView quantityTextView = dialogView.findViewById(R.id.quantityTextView);
        final ImageButton minusButton = dialogView.findViewById(R.id.minusButton);
        final ImageButton plusButton = dialogView.findViewById(R.id.plusButton);
        final Button saveButton = dialogView.findViewById(R.id.saveButton);
        final Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        medNameEditText.setText(medication.getMedName());
        brandEditText.setText(medication.getBrand());
        dosageEditText.setText(medication.getDosage());
        currentQuantity = medication.getQuantityOnHand();
        quantityTextView.setText(String.valueOf(currentQuantity));

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        minusButton.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                quantityTextView.setText(String.valueOf(currentQuantity));
            }
        });

        plusButton.setOnClickListener(v -> {
            if (currentQuantity < 10000) {
                currentQuantity++;
                quantityTextView.setText(String.valueOf(currentQuantity));
            } else {
                Toast.makeText(this, getString(R.string.error_quantity_too_large), Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(view -> {
            String medNameInput = medNameEditText.getText().toString();
            String brandInput = brandEditText.getText().toString();
            String dosageInput = dosageEditText.getText().toString();

            if (TextUtils.isEmpty(medNameInput) || TextUtils.isEmpty(brandInput) || TextUtils.isEmpty(dosageInput)) {
                Toast.makeText(this, getString(R.string.error_fill_all_fields), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!StringProcessor.isValidName(medNameInput)) {
                Toast.makeText(this, getString(R.string.error_invalid_med_name), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!StringProcessor.isValidName(brandInput)) {
                Toast.makeText(this, getString(R.string.error_invalid_brand_name), Toast.LENGTH_SHORT).show();
                return;
            }

            double dosageValue = StringProcessor.parseDosageValue(dosageInput);
            if (dosageValue <= 0) {
                Toast.makeText(this, getString(R.string.error_invalid_dosage_value), Toast.LENGTH_SHORT).show();
                return;
            }
            if (dosageValue > 5000) {
                Toast.makeText(this, getString(R.string.error_dosage_too_large), Toast.LENGTH_SHORT).show();
                return;
            }

            String medName = StringProcessor.toTitleCase(medNameInput);
            String brand = StringProcessor.toTitleCase(brandInput);
            String dosage = StringProcessor.formatDosage(dosageInput);

            medication.setMedName(medName);
            medication.setBrand(brand);
            medication.setDosage(dosage);
            medication.setQuantityOnHand(currentQuantity);

            showUpdateConfirmationDialog(medication);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void showSaveConfirmationDialog(final Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.med_add_confirmation_dialog, null);
        builder.setView(dialogView);

        final TextView messageTextView = dialogView.findViewById(R.id.confirmationMessageTextView);
        final Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        final Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        messageTextView.setText(getString(R.string.confirm_add_med, medication.getBrand()));
        positiveButton.setText(R.string.add);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        positiveButton.setOnClickListener(v -> {
            userViewModel.insertMedication(medication);
            Toast.makeText(this, getString(R.string.med_add_message), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showUpdateConfirmationDialog(final Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.med_update_confirmation_dialog, null);
        builder.setView(dialogView);

        final TextView messageTextView = dialogView.findViewById(R.id.confirmationMessageTextView);
        final Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        final Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        messageTextView.setText(getString(R.string.confirm_update_med, medication.getBrand()));
        positiveButton.setText(R.string.update);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        positiveButton.setOnClickListener(v -> {
            userViewModel.updateMedication(medication);
            Toast.makeText(this, getString(R.string.med_update_message), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            if (currentlySelectedMedication != null && currentlySelectedMedication.getMedID() == medication.getMedID()) {
                quantityTextView.setText(getString(R.string.quantity_on_hand_label, medication.getQuantityOnHand()));
            }
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteConfirmationDialog(final Medication medication) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.med_update_confirmation_dialog, null);
        builder.setView(dialogView);

        final TextView messageTextView = dialogView.findViewById(R.id.confirmationMessageTextView);
        final Button positiveButton = dialogView.findViewById(R.id.positiveButton);
        final Button negativeButton = dialogView.findViewById(R.id.negativeButton);

        messageTextView.setText(getString(R.string.confirm_delete_med, medication.getBrand()));
        positiveButton.setText(R.string.delete);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        positiveButton.setOnClickListener(v -> {
            userViewModel.deleteMedication(medication);
            Toast.makeText(this, getString(R.string.med_deleted_success, medication.getBrand()), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadMedicationNames();
        });

        negativeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showQuitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.general_confirm_dialog, null);
        builder.setView(dialogView);

        final Button yesButton = dialogView.findViewById(R.id.positiveButton);
        final Button noButton = dialogView.findViewById(R.id.negativeButton);
        final TextView messageTextView = dialogView.findViewById(R.id.confirmationMessageTextView);

        messageTextView.setText(R.string.quit_dialog);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        yesButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        noButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}