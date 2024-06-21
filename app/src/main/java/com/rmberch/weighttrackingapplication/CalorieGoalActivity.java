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

public class CalorieGoalActivity extends AppCompatActivity {
    EditText calorieEditText;
    Button submitCalorieButton;
    private int USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get user's ID
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_goal);

        //Bind Button and Edit Text
        submitCalorieButton = findViewById(R.id.submitCalorieGoal);
        calorieEditText = findViewById(R.id.calorieGoalEditText);

        //Function to call updateButtonState when text present in calorieEditText
        calorieEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateButtonState();
            }
        });

        //When submit CalorieButton is clicked
        submitCalorieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = null;
                int calorieGoal = -1;
                boolean success = false;

                //If calorieEditText has text
                if (calorieEditText != null) {
                    //Get text
                    text = calorieEditText.getText().toString();
                }
                try {
                    //Try to get integer value
                    calorieGoal = Integer.parseInt(text);
                } catch (NumberFormatException e) {
                    //Display message if error
                    Toast.makeText(CalorieGoalActivity.this, "Invalid Calorie Goal Entered. Try again.", Toast.LENGTH_SHORT).show();
                }

                //If calorieGoal has been reassigned
                if (calorieGoal > 0) {
                    //Submit calorie goal to database
                    WeightDatabase weightDatabase = new WeightDatabase(CalorieGoalActivity.this);
                    success = weightDatabase.setCalorieGoal(USER_ID, calorieGoal);
                }
                if (success) {
                    //Success message
                    Toast.makeText(CalorieGoalActivity.this, "Calorie Goal Successfully Entered", Toast.LENGTH_SHORT).show();
                } else {
                    //Failure message
                    Toast.makeText(CalorieGoalActivity.this, "Error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateButtonState() {
        //Enables submit button when goalEdit has text
        if (calorieEditText != null) {
            submitCalorieButton.setEnabled(!calorieEditText.getText().toString().isEmpty());

        }
    }

    //Closes activity
    public void onBackButtonClick(View view) {
        finish();
    }
}
