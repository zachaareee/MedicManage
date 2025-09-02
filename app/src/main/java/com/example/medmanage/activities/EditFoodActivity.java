package com.example.medmanage.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.R;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Food;

import java.util.concurrent.ExecutorService;

public class EditFoodActivity extends AppCompatActivity {

    private TextView foodNameText;
    private TextView quantityTextView;
    private Button decreaseButton, increaseButton, saveButton;

    private databaseMedicManage db;
    private Food currentFood;
    private int currentQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_food);

       db =  databaseMedicManage.getDatabase(getApplicationContext(), new databaseMedicManage.DatabaseCallback() {
            @Override
            public void onDatabaseReady(databaseMedicManage database) {
                db = database;
            }
        });
        foodNameText = findViewById(R.id.foodNameText);
        quantityTextView = findViewById(R.id.quantityTextView);
        decreaseButton = findViewById(R.id.decreaseButton);
        increaseButton = findViewById(R.id.increaseButton);
        saveButton = findViewById(R.id.saveButton);

        // Get the Food object that was passed from the list screen
        currentFood = (Food) getIntent().getSerializableExtra("FOOD_ITEM");

        if (currentFood != null) {
            currentQuantity = currentFood.getQuantity();
            foodNameText.setText(currentFood.getFoodName());
            updateQuantityText();
        }

        setupButtonClickListeners();
    }

    private void setupButtonClickListeners() {
        decreaseButton.setOnClickListener(v -> {
            if (currentQuantity > 0) {
                currentQuantity--;
                updateQuantityText();
            }
        });

        increaseButton.setOnClickListener(v -> {
            currentQuantity++;
            updateQuantityText();
        });

        saveButton.setOnClickListener(v -> {
            saveChangesToDatabase();
        });
    }

    private void updateQuantityText() {
        quantityTextView.setText(String.valueOf(currentQuantity));
    }

    private void saveChangesToDatabase() {
        // Update the food object with the new quantity
        currentFood.setQuantity(currentQuantity);

        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            // Save the updated object to the database
            db.foodDAO().updateFood(currentFood);

            // Show a confirmation message and close the screen
            runOnUiThread(() -> {
                Toast.makeText(this, "Quantity updated!", Toast.LENGTH_SHORT).show();
                finish(); // This closes the EditFoodActivity and returns to the list
            });
        });
    }
}