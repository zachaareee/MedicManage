package com.example.medmanage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout; // ADDED

import com.example.medmanage.R;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Food;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class FoodViewActivity extends AppCompatActivity {

    // CHANGED: Replaced Spinner with the new custom layout components
    private ConstraintLayout customSpinner;
    private TextView selectedItemTextView;

    private TextView nameTextView;
    private TextView typeTextView; //Brand
    private TextView quantityTextView;
    private ProgressBar progressBar;
    private TextView loadingText;
    private TextView errorText;

    private databaseMedicManage db;
    private Food selectedFood;

    //element that we will hide/show when loading
    private TextView header;
    private MaterialCardView detailsCard;
    private Button editButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make sure this layout name matches your XML file with the custom spinner
        setContentView(R.layout.food_list_view);

        //database
        db = databaseMedicManage.getDatabase(getApplicationContext());

        // UI Views
        // CHANGED: Find the new views for the custom spinner
        customSpinner = findViewById(R.id.customSpinner);
        selectedItemTextView = findViewById(R.id.selectedItemTextView);

        nameTextView = findViewById(R.id.nameTextView);
        typeTextView = findViewById(R.id.typeTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        errorText = findViewById(R.id.errorText);

        // Views that we will hide during loading
        header = findViewById(R.id.header);
        detailsCard = findViewById(R.id.detailsCard);
        editButton = findViewById(R.id.editButton);

        // String userType = getIntent().getStringExtra("USER_TYPE");
        String userType = "Administrator";

        if (userType.equals("Administrator")) {
            editButton.setVisibility(View.VISIBLE);
        } else {
            editButton.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(v -> {
            if (selectedFood != null) {
                Intent intent = new Intent(FoodViewActivity.this, EditFoodActivity.class);
                intent.putExtra("FOOD_ITEM", selectedFood);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select a food item first", Toast.LENGTH_SHORT).show();
            }
        });

        // Get Data to populate
        fetchFoodData();
    }

    // Refresh Screen
    @Override
    protected void onResume() {
        super.onResume();
        fetchFoodData();
    }

    private void fetchFoodData() {
        showLoading(true);
        ExecutorService executorService = databaseMedicManage.databaseWriteExecutor;

        executorService.execute(() -> {
            try {
                FoodDAO foodDAO = db.foodDAO();
                List<Food> dataFromDb = foodDAO.getAllSortedByQuantityDesc();
                runOnUiThread(() -> {
                    // CHANGED: Call the new setup method
                    setupCustomSpinner(dataFromDb);
                    showLoading(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::showError);
            }
        });
    }

    // handle the custom spinner logic.
    private void setupCustomSpinner(List<Food> foodList) {
        if (isFinishing() || isDestroyed() || foodList == null || foodList.isEmpty()) {
            if (foodList != null && foodList.isEmpty()) {
                // Handle case where there are no food items
                selectedItemTextView.setText(R.string.no_food_items);
                updateDetails(null); // Clear details
            }
            return;
        }

        // Set the initial selection to the first item
        selectedFood = foodList.get(0);
        updateDetails(selectedFood);
        selectedItemTextView.setText(selectedFood.toString()); // Assumes Food model has a good toString() method

        // Set the click listener for the custom spinner layout
        customSpinner.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(FoodViewActivity.this, v);

            // Populate the menu with food names
            for (Food food : foodList) {
                popupMenu.getMenu().add(food.toString());
            }

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedTitle = item.getTitle().toString();
                // Find the food object that matches the selected title
                for (Food food : foodList) {
                    if (food.toString().equals(selectedTitle)) {
                        selectedFood = food;
                        break; // Exit loop once found
                    }
                }

                // Update the UI
                selectedItemTextView.setText(selectedFood.toString());
                updateDetails(selectedFood);
                return true;
            });

            popupMenu.show();
        });
    }

    // This method remains unchanged
    private void updateDetails(Food food) {
        if (food != null) {
            nameTextView.setText(food.getFoodName());
            typeTextView.setText(food.getFoodBrand());
            quantityTextView.setText(String.valueOf(food.getQuantity()));
        } else {
            // Clear text if no food is selected
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

        // Show/hide main content
        header.setVisibility(contentVisibility);
        // CHANGED: Toggle visibility of the custom spinner layout
        customSpinner.setVisibility(contentVisibility);
        detailsCard.setVisibility(contentVisibility);
        editButton.setVisibility(contentVisibility);

        errorText.setVisibility(View.GONE);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);

        header.setVisibility(View.GONE);
        // CHANGED: Hide the custom spinner layout on error
        customSpinner.setVisibility(View.GONE);
        detailsCard.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        errorText.setVisibility(View.VISIBLE);
    }
}