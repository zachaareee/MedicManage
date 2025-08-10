package com.example.medmanage.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.medmanage.R;
import com.example.medmanage.dao.FoodDAO;
import com.example.medmanage.database.databaseMedicManage;
import com.example.medmanage.model.Food;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class FoodViewActivity extends AppCompatActivity {

    private Spinner spinner;
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
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_view); // we will use the food_list_view xml file for this view

        //database
        db = databaseMedicManage.getDatabase(getApplicationContext());

        //UI Views
        spinner = findViewById(R.id.spinner);
        nameTextView = findViewById(R.id.nameTextView);
        typeTextView = findViewById(R.id.typeTextView);
        quantityTextView = findViewById(R.id.quantityTextView);
        progressBar = findViewById(R.id.progressBar);
        loadingText = findViewById(R.id.loadingText);
        errorText = findViewById(R.id.errorText);

        //Views that we will hide during loading
        header = findViewById(R.id.header);
        detailsCard = findViewById(R.id.detailsCard);
        editButton = findViewById(R.id.editButton);

        //String userType = getIntent().getStringExtra("USER_TYPE");
        String userType = "Administrator";

        if(userType.equals("Administrator")){
            //Make the button visible for the student
            editButton.setVisibility(View.VISIBLE);
        }
        else{
            //Hide the button for the student
            editButton.setVisibility(View.GONE);
        }

        editButton.setOnClickListener(v->{
            if(selectedFood!=null){
                Intent intent = new Intent(FoodViewActivity.this,EditFoodActivity.class);
                intent.putExtra("FOOD_ITEM",selectedFood);
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"Please select a food item first", Toast.LENGTH_SHORT).show();
            }
        });

        //Get Data to populate
        fetchFoodData();



    }
    //Refresh Screen
    @Override
    protected void onResume(){
        super.onResume();
        fetchFoodData();
    }

    private void fetchFoodData() {
        showLoading(true);
        ExecutorService executorService = databaseMedicManage.databaseWriteExecutor;

        executorService.execute(() ->{
            try{
                FoodDAO foodDAO = db.foodDAO();

                List<Food> dataFromdb = foodDAO.getAllSortedByQuantityDesc();

                runOnUiThread(()-> {
                    setupSpinner(dataFromdb);
                    showLoading(false);
                        });

            }
            catch (Exception e){
                e.printStackTrace();
                runOnUiThread(this::showError);
            }


        });
    }

    //Sets up the spinner with the fetched food data
    // this is like a vending machine the  Array Adapter acts as a stocker for our vending machine

    private void setupSpinner(List<Food> foodList) {
        //If the activity is destroyed no need to set up
        if(isFinishing() || isDestroyed()){
            return;
        }

        ArrayAdapter <Food> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,foodList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                selectedFood = (Food)adapterView.getItemAtPosition(position);
                updateDetails(selectedFood);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Interface call back

            }
        });

        if(!foodList.isEmpty()){
            updateDetails(foodList.get(0));
        }

    }


    //Will update card view with information of the selected item
    private void updateDetails(Food food) {
        if (food != null) {
            nameTextView.setText(food.getFoodName());
            typeTextView.setText(food.getFoodBrand());
            quantityTextView.setText(String.valueOf(food.getQuantity()));
        }
    }

    private void showLoading(boolean isLoading) {
        int contentVisibility = isLoading ? View.GONE:View.VISIBLE;
        int loadingVisibility = isLoading ? View.VISIBLE: View.GONE;

            //Loading...indicators
            progressBar.setVisibility(loadingVisibility);
            loadingText.setVisibility(loadingVisibility);

            //Show/hide main content
            header.setVisibility(contentVisibility);
            spinner.setVisibility(contentVisibility);
            detailsCard.setVisibility(contentVisibility);
            editButton.setVisibility(contentVisibility);

            //Always hide error when loadimg/showing content
            errorText.setVisibility(View.GONE);






    }
    private void showError(){
        progressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);

        header.setVisibility(View.GONE);
        spinner.setVisibility(View.GONE);
        detailsCard.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);

        errorText.setVisibility(View.VISIBLE);

    }


}
