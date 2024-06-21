package com.rmberch.weighttrackingapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Class to LogWeight
public class LogWeightActivity extends AppCompatActivity {
    //Variable declaration
    int USER_ID;
    EditText weightEdit, dateEdit;
    Button submitWeight;
    String DATE;
    float weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_weight);

        //Retrieve user id from intent
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);
        DATE = intent.getStringExtra("DATE");

        //Bind Layout elements
        weightEdit = findViewById(R.id.logWeightEditText);
        dateEdit = findViewById(R.id.logDateEditText);
        submitWeight = findViewById(R.id.submitWeightButton);

        dateEdit.setText(DATE);

        //Text Changed listeners to enable buttons if text
        weightEdit.addTextChangedListener(new TextWatcher() {
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
        dateEdit.addTextChangedListener(new TextWatcher() {
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

        //When submit is clicked
        submitWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Declare/define variables
                boolean success;
                WeightDatabase weightDatabase = new WeightDatabase(LogWeightActivity.this); //database instance
                //Get text from fields
                String weightText = weightEdit.getText().toString();
                DATE = dateEdit.getText().toString();

                try {
                    //try to get float from string
                    weight = Float.parseFloat(weightText);
                }
                catch (NumberFormatException e) {
                    //If number error, display message
                    Toast.makeText(LogWeightActivity.this, "Invalid weight. Try again.", Toast.LENGTH_SHORT).show();
                }

                //Make DataModel with input
                DataModel dataModel = new DataModel(DATE, weight);
                //Add to database
                success = weightDatabase.addWeightRecord(dataModel, USER_ID);

                //If successful
                if (success) {
                    //Success message
                    Toast.makeText(LogWeightActivity.this, "Weight successfully logged.", Toast.LENGTH_SHORT).show();
                    //Check if weight matches with goalWeight
                    checkWeightMatch(weightDatabase, weightText);
                }
                else {
                    //Unsuccessful log attempt
                    Toast.makeText(LogWeightActivity.this, "Error logging weight. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Check if weight matches goal weight
    private void checkWeightMatch(WeightDatabase weightDatabase, String weightText) {
        //get goal weight
        String goalWeight = weightDatabase.getGoalWeight(USER_ID);
        //Empty string for phone number
        String phone = null;
        //if matches
        if (weightText.equals(goalWeight)) {
            //if has permission
            if (checkPermission()) {
                //get phone number
                phone = weightDatabase.getPhoneNumber(USER_ID);
                //if there is phone number
                if (phone != null) {
                    //Send message function call
                    sendSms(phone);
                }
            }
            //If no permissions, display a toast message
            else {
                Toast.makeText(LogWeightActivity.this, "Congratulations on reaching your weight goal!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    //Send message function receives string for phone number
    private void sendSms(String phone) {
        //String for message
        String message = "Congratulations on reaching your weight goal!";
        try {
            //Try to Send message
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
        }
        catch (Exception e) {
            //Error message
            Toast.makeText(LogWeightActivity.this, "Error sending SMS message.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        // Check if sms permission is granted
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    //When closed
    public void onBackButtonClick(View view) {
        //return to previous screen, passing date and weight to update RecyclerView
        Intent resultIntent = new Intent();
        resultIntent.putExtra("date", DATE);
        resultIntent.putExtra("weight", weight);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    //Enable buttons when fields have text
    private void updateButtonState() {
        if (weightEdit != null && dateEdit != null) {
            submitWeight.setEnabled(!weightEdit.getText().toString().isEmpty() && !dateEdit.getText().toString().isEmpty());
        }
    }
}
