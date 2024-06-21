package com.rmberch.weighttrackingapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Java code for Screen to insert/update Goal Weight
public class GoalWeightActivity extends AppCompatActivity {
    //Variable declaration
    EditText goalEdit;
    Button setGoal;
    private int USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_weight);

        //Get user id
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);

        //Bind layout elements
        goalEdit = findViewById(R.id.goalWeightEditText);
        setGoal = findViewById(R.id.submitGoalWeightButton);



        //Function to call updateButtonState when text present in goalEdit
        goalEdit.addTextChangedListener(new TextWatcher() {
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

        //When setGoal is clicked
        setGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 String text = null;
                 float goalWeight = -1;
                 boolean success = false;

                 //If goalEdit has text
                 if (goalEdit != null) {
                     //Get text
                     text = goalEdit.getText().toString();
                 }
                 try {
                     //Try to get float value
                     goalWeight = Float.parseFloat(text);
                 } catch (NumberFormatException e) {
                     //Display message if error
                     Toast.makeText(GoalWeightActivity.this, "Invalid Weight Entered. Try again.", Toast.LENGTH_SHORT).show();
                 }

                 //If goalWeight has been reassigned
                 if (goalWeight != -1) {
                     //Submit goal weight to database
                     WeightDatabase weightDatabase = new WeightDatabase(GoalWeightActivity.this);
                     success = weightDatabase.setGoalWeight(USER_ID, goalWeight);
                 }
                 if (success) {
                     //Success message
                     Toast.makeText(GoalWeightActivity.this, "Goal Weight Successfully Entered.", Toast.LENGTH_SHORT).show();
                 } else {
                     //Failure message
                     Toast.makeText(GoalWeightActivity.this, "Error. Please try again.", Toast.LENGTH_SHORT).show();
                 }

            }
        });

    }

    private void updateButtonState() {
        //Enables submit button when goalEdit has text
        if (goalEdit != null) {
            setGoal.setEnabled(!goalEdit.getText().toString().isEmpty());

        }
    }

    //Closes activity
    public void onBackButtonClick(View view) {
        finish();
    }
}