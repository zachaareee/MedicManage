package com.example.medmanage.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.medmanage.R;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Food;
import com.google.android.material.card.MaterialCardView;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class FoodViewActivity extends AppCompatActivity {
    private ConstraintLayout customSpinner;
    private TextView selectedItemTextView;
    private TextView nameTextView;
    private TextView typeTextView;
    private TextView quantityTextView;
    private ProgressBar progressBar;
    private TextView loadingText;
    private TextView errorText;
    private databaseMedicManage db;
    private Food selectedFood;
    private TextView header;
    private MaterialCardView detailsCard;
    private Button updateButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_view);
        db = databaseMedicManage.getDatabase(getApplicationContext());

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
        updateButton = findViewById(R.id.updateButton);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> showQuitConfirmationDialog());

        // MODIFIED: This now calls the dialog method instead of starting a new activity
        updateButton.setOnClickListener(v -> {
            if (selectedFood != null) {
                showEditFoodDialog(selectedFood);
            } else {
                Toast.makeText(this, "Please select a food item first", Toast.LENGTH_SHORT).show();
            }
        });

        String userType = getIntent().getStringExtra("USER_TYPE");
        if ("student".equalsIgnoreCase(userType)) {
            updateButton.setVisibility(View.GONE);
        } else if ("nurse".equalsIgnoreCase(userType)) {
            updateButton.setVisibility(View.VISIBLE);
        }
        fetchFoodData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No need to always fetch here anymore as the dialog will refresh data upon success.
        // If you still need it for other reasons (e.g., coming back from another app), you can leave it.
        // For now, let's assume the main refresh happens after editing.
    }

    private void fetchFoodData() {
        showLoading(true);
        ExecutorService executorService = databaseMedicManage.databaseWriteExecutor;
        executorService.execute(() -> {
            try {
                FoodDAO foodDAO = db.foodDAO();
                List<Food> dataFromDb = foodDAO.getAllSortedByQuantityDesc();
                runOnUiThread(() -> {
                    setupCustomSpinner(dataFromDb);
                    showLoading(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::showError);
            }
        });
    }

    /**
     * ADDED: This method contains all the logic from the old EditFoodActivity.
     * It creates and displays an AlertDialog to edit the food item's quantity.
     * @param foodToEdit The Food object to be modified.
     */
    private void showEditFoodDialog(final Food foodToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.food_edit_dialog, null);
        builder.setView(dialogView);

        // Find views inside the dialog layout
        TextView dialogTitleTextView = dialogView.findViewById(R.id.dialogTitleTextView);
        TextView quantityTextViewDialog = dialogView.findViewById(R.id.quantityTextView);
        ImageButton minusButton = dialogView.findViewById(R.id.minusButton);
        ImageButton plusButton = dialogView.findViewById(R.id.plusButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        Button saveButton = dialogView.findViewById(R.id.saveButton);

        // Set initial values from the food item
        dialogTitleTextView.setText(foodToEdit.getFoodName());
        final int[] currentQuantity = {foodToEdit.getQuantity()}; // Use array to be mutable in lambda
        quantityTextViewDialog.setText(String.valueOf(currentQuantity[0]));

        final AlertDialog dialog = builder.create();

        // This makes the dialog's background transparent to show the rounded corners from the XML
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Set up button listeners
        minusButton.setOnClickListener(v -> {
            if (currentQuantity[0] > 0) {
                currentQuantity[0]--;
                quantityTextViewDialog.setText(String.valueOf(currentQuantity[0]));
            }
        });

        plusButton.setOnClickListener(v -> {
            currentQuantity[0]++;
            quantityTextViewDialog.setText(String.valueOf(currentQuantity[0]));
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        saveButton.setOnClickListener(v -> {
            foodToEdit.setQuantity(currentQuantity[0]);

            ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
            executor.execute(() -> {
                db.foodDAO().updateFood(foodToEdit);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Quantity updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // Crucial: Refresh the data in this activity after saving
                    fetchFoodData();
                });
            });
        });

        dialog.show();
    }

    private void setupCustomSpinner(List<Food> foodList) {
        if (isFinishing() || isDestroyed() || foodList == null || foodList.isEmpty()) {
            if (foodList != null && foodList.isEmpty()) {
                selectedItemTextView.setText(R.string.no_food_items);
                updateDetails(null);
            }
            return;
        }

        selectedFood = foodList.get(0);
        updateDetails(selectedFood);
        selectedItemTextView.setText(selectedFood.toString());

        customSpinner.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(FoodViewActivity.this, v);
            for (Food food : foodList) {
                popupMenu.getMenu().add(food.toString());
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedTitle = item.getTitle().toString();
                for (Food food : foodList) {
                    if (food.toString().equals(selectedTitle)) {
                        selectedFood = food;
                        break;
                    }
                }
                selectedItemTextView.setText(selectedFood.toString());
                updateDetails(selectedFood);
                return true;
            });

            popupMenu.show();
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

        // Set a specific message for quitting
        // You should add this string to your strings.xml
        // messageTextView.setText(getString(R.string.confirm_quit));
        // For now, I'll use the default text from the layout or hardcode it:
        messageTextView.setText("Are you sure you want to quit?");

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

    private void updateDetails(Food food) {
        if (food != null) {
            nameTextView.setText(food.getFoodName());
            typeTextView.setText(food.getFoodBrand());
            quantityTextView.setText(String.valueOf(food.getQuantity()));
        } else {
            nameTextView.setText("");
            typeTextView.setText("");
            quantityTextView.setText("");
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
        findViewById(R.id.buttonContainer).setVisibility(contentVisibility);

        errorText.setVisibility(View.GONE);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        header.setVisibility(View.GONE);
        customSpinner.setVisibility(View.GONE);
        detailsCard.setVisibility(View.GONE);
        findViewById(R.id.buttonContainer).setVisibility(View.GONE);
        errorText.setVisibility(View.VISIBLE);
    }
}
