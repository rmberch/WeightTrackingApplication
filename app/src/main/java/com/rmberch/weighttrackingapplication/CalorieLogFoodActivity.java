package com.rmberch.weighttrackingapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CalorieLogFoodActivity extends AppCompatActivity {
    int USER_ID;
    String foodName, carbString, fatString, proteinString, servingString, DATE;
    double carbs, fats, protein, servings;
    EditText foodNameEditText, carbsEditText, fatsEditText, proteinEditText, servingsEditText;
    Button submitFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_log_food);

        //Retrieve user id from intent
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);
        DATE = intent.getStringExtra("DATE");

        foodNameEditText = findViewById(R.id.foodNameEditText);
        carbsEditText = findViewById(R.id.insertCarbsEditText);
        fatsEditText = findViewById(R.id.insertFatsEditText);
        proteinEditText = findViewById(R.id.insertProteinEditText);
        servingsEditText = findViewById(R.id.insertServingsEditText);
        submitFood = findViewById(R.id.submitFoodButton);

        //Text Changed listeners to enable buttons if text
        foodNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });
        carbsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });
        fatsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });
        proteinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });
        servingsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });

        submitFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Declare/define variables
                boolean success;
                WeightDatabase weightDatabase = new WeightDatabase(CalorieLogFoodActivity.this);

                //Get text from fields
                foodName = foodNameEditText.getText().toString();
                carbString = carbsEditText.getText().toString();
                fatString = fatsEditText.getText().toString();
                proteinString = proteinEditText.getText().toString();
                servingString = servingsEditText.getText().toString();

                try {
                    carbs = Double.parseDouble(carbString);
                    fats = Double.parseDouble(fatString);
                    protein = Double.parseDouble(proteinString);
                    servings = Double.parseDouble(servingString);
                }
                catch (NumberFormatException e) {
                    //If number error, display message
                    Toast.makeText(CalorieLogFoodActivity.this, "Invalid values. Try again.", Toast.LENGTH_SHORT).show();
                }

                FoodModel foodModel = new FoodModel(carbs, fats, protein, servings, foodName);
                success = weightDatabase.addFoodRecord(USER_ID, foodModel, DATE);

                if (success) {
                    //Success message
                    Toast.makeText(CalorieLogFoodActivity.this, "Food successfully logged.", Toast.LENGTH_SHORT).show();
                } else {
                    //Unsuccessful log attempt
                    Toast.makeText(CalorieLogFoodActivity.this, "Error logging food. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //When closed
    public void onBackButtonClick(View view) {
        //return to previous screen, passing date and weight to update RecyclerView
        Intent resultIntent = new Intent();
        resultIntent.putExtra("carbs", carbs);
        resultIntent.putExtra("fats", fats);
        resultIntent.putExtra("protein", protein);
        resultIntent.putExtra("servings", servings);
        resultIntent.putExtra("foodName", foodName);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //Enable buttons when fields have text
    private void updateButtonState() {
        if (foodNameEditText != null && carbsEditText != null && fatsEditText != null && proteinEditText != null && servingsEditText != null ) {
            submitFood.setEnabled(!foodNameEditText.getText().toString().isEmpty() && !carbsEditText.getText().toString().isEmpty() && !fatsEditText.getText().toString().isEmpty()
                    && !proteinEditText.getText().toString().isEmpty() && !servingsEditText.getText().toString().isEmpty());
        }
    }
}
