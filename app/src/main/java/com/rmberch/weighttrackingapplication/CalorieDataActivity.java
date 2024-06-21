package com.rmberch.weighttrackingapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalorieDataActivity extends AppCompatActivity {
    int USER_ID;
    double CALORIES_CONSUMED, CALORIES_REMAINING = 0;
    String DATE, CALORIE_GOAL;
    private List<FoodModel> foodList;
    private CalorieGridAdapter calorieAdapter;
    TextView calorieCountTextView, dateDisplayTextView;
    RecyclerView calorieRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_data);

        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);
        DATE = intent.getStringExtra("DATE");

        calorieCountTextView = findViewById(R.id.CalorieCountDisplay);
        dateDisplayTextView = findViewById(R.id.DateDisplay);

        //Retrieve foodList
        foodList = getCalorieHistory(DATE);
        setCaloricDisplayText();
        setDateDisplayText(DATE);

        //Find Recycler View
        calorieRecyclerView = findViewById(R.id.calorieRecyclerView);
        //Bind foodList to calorieAdapter
        calorieAdapter = new CalorieGridAdapter(foodList, new CalorieGridAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
        //Bind adapter to recycler view
        calorieRecyclerView.setAdapter(calorieAdapter);

        //Bind add food item button
        Button addFoodButton = findViewById(R.id.addCalorieButton);
        Button changeDateButton = findViewById(R.id.changeDateButton);

        addFoodButton.setOnClickListener(v -> openAddFoodItemScreen());
        changeDateButton.setOnClickListener(v -> openChangeDateDialog());

    }

    private void setDateDisplayText(String date) {
        dateDisplayTextView.setText(date);
    }

    private void openChangeDateDialog() {
        //Create Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Date");

        //Get layout inflater and bind dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_date_dialog, null);
        builder.setView(dialogView);

        EditText editDate = dialogView.findViewById(R.id.editDateEditText);
        editDate.setText(dateDisplayTextView.getText());

        //Code to save the record
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the edited values
                String date = editDate.getText().toString();

                if (!date.isEmpty()) {
                    changeDate(date);
                    //Message output that the update was successful
                    Toast.makeText(CalorieDataActivity.this, "Date successfully changed.", Toast.LENGTH_SHORT).show();
                    //Close dialog
                    dialog.dismiss();
                    }
                else {
                    //Error message that the update failed
                    Toast.makeText(CalorieDataActivity.this, "Could not change date. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Create cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Closes dialog
                dialog.dismiss();
            }
        });
        //Show dialog
        builder.show();
    }

    private void changeDate(String date) {
        //Change Date variable to new date
        DATE = date;
        //Delete all data from foodList and get new data from Database
        foodList.clear();
        foodList = getCalorieHistory(DATE);
        //Update the Grid Adapter and Recycler view with new data
        calorieAdapter.setFoodList(foodList);
        calorieRecyclerView.setAdapter(calorieAdapter);
        //Update Text displays with new data
        setCaloricDisplayText();
        setDateDisplayText(DATE);
    }

    private void setCaloricDisplayText() {
        String CalorieCountDisplay;
        CALORIES_CONSUMED = 0;
        //Calculate total calories consumed today
        for (int i = 0; i < foodList.size(); ++i){
            FoodModel cur = foodList.get(i);
            CALORIES_CONSUMED += cur.getCalories();
        }
        //Retrieve Calorie goal from database and calculate calories remaining
        CALORIE_GOAL = getCalorieGoal(USER_ID);
       //If no value is set, display message telling user to set one
        if (CALORIE_GOAL == null) {
            CalorieCountDisplay = "Please set a daily caloric goal before logging calories.";
        }
        //Else get the number, calculate the calories remaining, and display properly
        else {
            int CALORIE_GOAL_INT = Integer.parseInt(CALORIE_GOAL);
            CALORIES_REMAINING = CALORIE_GOAL_INT - CALORIES_CONSUMED;

            //Create and assign calorie calculation display
            CalorieCountDisplay = CALORIE_GOAL + " - " + Double.toString(CALORIES_CONSUMED) + " = " + Double.toString(CALORIES_REMAINING);
        }
        // Set calorie count text
        calorieCountTextView.setText(CalorieCountDisplay);
    }

    public void onBackButtonClick(View view) {
        //Closes activity
        finish();
    }
    private void openAddFoodItemScreen() {
        Intent intent = new Intent(this, CalorieLogFoodActivity.class);
        intent.putExtra("USER_ID", USER_ID);
        intent.putExtra("DATE", DATE);
        LogFoodActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> LogFoodActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            double carbs = data.getDoubleExtra("carbs", -1);
                            double fats = data.getDoubleExtra("fats", -1);
                            double protein = data.getDoubleExtra("protein", -1);
                            double servings = data.getDoubleExtra("servings", -1);
                            String foodName = data.getStringExtra("foodName");
                            FoodModel newFM = new FoodModel(carbs, fats, protein, servings, foodName);

                            if (newFM.getFoodName() != null) {
                                foodList.add(newFM);
                                calorieAdapter.notifyItemInserted(foodList.size());
                                setCaloricDisplayText();
                            }
                        }
                    }
                }
            });

    private String getCalorieGoal(int ID) {
        String calorieGoal;
        WeightDatabase weightDatabase = new WeightDatabase(CalorieDataActivity.this);
        calorieGoal = weightDatabase.getCalorieGoal(ID);
        return calorieGoal;
    }

    private List<FoodModel> getCalorieHistory(String date) {
        //NEEDS TO BE IMPLEMENTED TO RETRIEVE RECORDS FROM DATABASE
        //CURRENTLY CREATES PLACE HOLDER DATA
        List<FoodModel> calorieHistory = null;
        WeightDatabase weightDatabase = new WeightDatabase(CalorieDataActivity.this);
        calorieHistory = weightDatabase.getCalorieHistory(USER_ID, date);
        return calorieHistory;
    }

    private int getItemCount() { return foodList.size(); }

    private void removeItem(int position) {
        FoodModel removeFood = foodList.get(position);
        boolean removed;
        WeightDatabase weightDatabase = new WeightDatabase(CalorieDataActivity.this);
        removed = weightDatabase.removeFoodRecord(USER_ID, removeFood, DATE);

        if (removed) {
            foodList.remove(position);
            calorieAdapter.notifyItemRemoved(position + 1);
            calorieAdapter.notifyItemRangeChanged(position + 1, getItemCount());
            Toast.makeText(CalorieDataActivity.this, "Record successfully deleted.", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(CalorieDataActivity.this, "Error deleting record. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

}
