package com.example.medmanage.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medmanage.R;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Food;

import java.util.concurrent.ExecutorService;

public class EditFoodActivity extends AppCompatActivity {

    private TextView foodNameText;
    private TextView quantityTextView;
    private ImageButton decreaseButton, increaseButton;
    private Button saveButton, cancelButton;

    private databaseMedicManage db;
    private Food currentFood;
    private int currentQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_edit_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setFinishOnTouchOutside(false);

        db = databaseMedicManage.getDatabase(getApplicationContext());
        foodNameText = findViewById(R.id.dialogTitleTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        decreaseButton = findViewById(R.id.minusButton);
        increaseButton = findViewById(R.id.plusButton);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton); // <-- ADDED THIS LINE

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

        cancelButton.setOnClickListener(v -> {
            finish(); // Closes the current screen
        });
    }

    private void updateQuantityText() {
        quantityTextView.setText(String.valueOf(currentQuantity));
    }

    private void saveChangesToDatabase() {
        if (currentFood == null) return;

        currentFood.setQuantity(currentQuantity);

        ExecutorService executor = databaseMedicManage.databaseWriteExecutor;
        executor.execute(() -> {
            db.foodDAO().updateFood(currentFood);

            runOnUiThread(() -> {
                Toast.makeText(this, "Quantity updated!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}