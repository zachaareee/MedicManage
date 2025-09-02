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
    private Button editButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_view);
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
        editButton = findViewById(R.id.editButton);

        String userType = getIntent().getStringExtra("USER_TYPE");

        if ("Administrator".equalsIgnoreCase(userType) || "nurse".equalsIgnoreCase(userType)) {
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

        fetchFoodData();
    }

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
                    setupCustomSpinner(dataFromDb);
                    showLoading(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::showError);
            }
        });
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