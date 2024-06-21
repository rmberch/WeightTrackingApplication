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


public class DataActivity extends AppCompatActivity {
    //Class variable declaration
    int USER_ID;
    String DATE;
    private List<DataModel> dataList;
    private RecyclerView recyclerView;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        //Get USER ID from intent
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);
        DATE = intent.getStringExtra("DATE");

        //Get goal weight from database and set to TextView
        String goalWeightText = "Goal Weight: " + getGoalWeight();
        TextView goalWeightTextView = findViewById(R.id.goalWeightTextView);
        goalWeightTextView.setText(goalWeightText);

        //Get Weight Records from database
        dataList = getHistory();

        //Find Recycler view
        recyclerView = findViewById(R.id.recyclerView);
        //Bind dataList to adapter
        adapter = new GridAdapter(dataList, new GridAdapter.OnItemClickListener() {
            //Call function to remove item
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }

            //Call function to edit item
            @Override
            public void onEditClick(int position) {
                showEditDialog(position);
            }
        });

        //Bind adapter to recycler view
        recyclerView.setAdapter(adapter);

        //Bind add weight button and OnClickListener
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> openAddDataScreen());
    }

    //Function to show Edit Dialog
    private void showEditDialog(int position) {

        //Create Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Record");

        //Get layout inflater and bind dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_dialog_layout, null);
        builder.setView(dialogView);

        //Retrieve record to be edited
        DataModel edit = dataList.get(position);

        //Bind date EditText and set text to record's date
        EditText editDate = dialogView.findViewById(R.id.editDateEditText);
        editDate.setText(edit.getDate());

        //Bind Weight EditText and set text to record's weight
        EditText editWeight = dialogView.findViewById(R.id.editWeightEditText);
        editWeight.setText(Float.toString(edit.getWeight()));

        //Code to save the record
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve the edited values
                String weightText = editWeight.getText().toString();
                String date = editDate.getText().toString();
                float weight = -1;

                //If weightText is not empty
                if (!weightText.isEmpty()) {
                    try {
                        //Try to assign float with text
                        weight = Float.parseFloat(weightText);
                    }
                    catch  (NumberFormatException e) {
                        //Display message if error
                        Toast.makeText(DataActivity.this, "Invalid weight. Try again.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //If text is empty, display message
                    Toast.makeText(DataActivity.this, "Enter a weight. Try again.", Toast.LENGTH_SHORT).show();
                }

                //If date and weight are not default
                if (!date.isEmpty() && weight != -1) {
                    //If statement tells database to update, returns true if successful
                    if (updateWeightRecord(USER_ID, edit, date, weight)) {
                        //Update dataList entry
                        dataList.set(position, new DataModel(date, weight));
                        //Tell adapter to update
                        adapter.notifyItemChanged(position + 1);
                        //Message output that the update was successful
                        Toast.makeText(DataActivity.this, "Record successfully updated.", Toast.LENGTH_SHORT).show();
                        //Close dialog
                        dialog.dismiss();
                    }
                    else {
                        //Error message that the update failed
                        Toast.makeText(DataActivity.this, "Could not update record. Try again.", Toast.LENGTH_SHORT).show();
                    }
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

    private List<DataModel> getHistory() {
        //Retrieves user's weight records from database
        List<DataModel> history;
        WeightDatabase weightDatabase = new WeightDatabase(DataActivity.this);
        history = weightDatabase.getHistory(USER_ID);
        return history;
    }

    private String getGoalWeight() {
        //Retrieves user's goal weight from database
        String goalWeight;
        WeightDatabase weightDatabase = new WeightDatabase(DataActivity.this);
        goalWeight = weightDatabase.getGoalWeight(USER_ID);
        return goalWeight;
    }

    public void onBackButtonClick(View view) {
        //Closes activity
        finish();
    }


    private void removeItem(int position) {
        //Gets data to remove
        DataModel removeData = dataList.get(position);
        Boolean removed;

        //Removes database
        WeightDatabase weightDatabase = new WeightDatabase(DataActivity.this);
        removed = weightDatabase.removeWeightRecord(USER_ID, removeData);

        //If successful, removed from dataList and recyclerView, then update adapter so buttons are programmed for correct position
        if (removed) {
            dataList.remove(position);
            adapter.notifyItemRemoved(position + 1);
            adapter.notifyItemRangeChanged(position + 1, getItemCount());
            Toast.makeText(DataActivity.this, "Record successfully deleted.", Toast.LENGTH_SHORT).show();
        }
        else { //Display error message
            Toast.makeText(DataActivity.this, "Error deleting record. Try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private int getItemCount() {
        return dataList.size();
    }

    //Open Log Data Screen
    private void openAddDataScreen() {
        Intent intent = new Intent(this, LogWeightActivity.class);
        intent.putExtra("USER_ID", USER_ID);
        intent.putExtra("DATE", DATE);
        LogWeightActivityResultLauncher.launch(intent);
    }

    //Launcher function to open LogWeightActivity to update the recyclerView with new record
    private ActivityResultLauncher<Intent> LogWeightActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String date = data.getStringExtra("date");
                            float weight = data.getFloatExtra("weight", -1);
                            DataModel newDM = new DataModel(date, weight);

                            if (newDM.getWeight() != 0.0) {
                                dataList.add(newDM);
                                adapter.notifyItemInserted(dataList.size());
                            }
                        }
                    }
                }
            });

    //Function that passes new and old data to update weight record
    private boolean updateWeightRecord(int userId, DataModel oldData, String newDate, float newWeight) {
        Boolean success = false;
        WeightDatabase weightDatabase = new WeightDatabase(DataActivity.this);
        String oldDate = oldData.getDate();
        float oldWeight = oldData.getWeight();
        success = weightDatabase.updateWeightRecord(userId, oldDate, newDate, newWeight);
        return success;
    }
}

