package com.rmberch.weighttrackingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//Code for Home Screen
public class HomeActivity extends AppCompatActivity {
    //Variable declaration
    private static final int SMS_PERMISSION_REQUEST_CODE = 100;
    private int USER_ID;
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Get user's ID
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);
        date = getDate();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Bind Buttons
        Button dataButton = findViewById(R.id.weightDataButton);
        Button goalWeightButton = findViewById(R.id.setGoalWeightButton);
        Button calorieGoalButton = findViewById(R.id.setCalorieGoalButton);
        Button logCalorieButton = findViewById(R.id.CalorieDataButton);
        Button smsNotification = findViewById(R.id.allowSMSNotificationButton);

        Button phoneNumber = findViewById(R.id.addPhoneNumber);
        phoneNumber.setVisibility(View.GONE);

        //Bind click listeners
        dataButton.setOnClickListener(v -> openDataActivity(USER_ID));
        goalWeightButton.setOnClickListener(v -> openGoalWeightActivity(USER_ID));
        calorieGoalButton.setOnClickListener(v -> openCalorieGoalActivity(USER_ID));
        logCalorieButton.setOnClickListener(v -> openCalorieDataActivity(USER_ID));
        phoneNumber.setOnClickListener(v -> openPhoneNumberActivity(USER_ID));


        //If sms permission enabled
        if(checkPermission()) {
            //Disable smsNotification button
            smsNotification.setVisibility(View.GONE);
            //If no phone number for user
            if (!checkPhoneNumber()) {
                //Enable phoneNumber button
                phoneNumber.setVisibility(View.VISIBLE);
            }
        }

    }

    private String getDate() {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Format the date
        return  today.format(formatter);
    }


    //Function to see if phone number in database
    private boolean checkPhoneNumber() {
        boolean check = true;
        WeightDatabase weightDatabase = new WeightDatabase(HomeActivity.this);
        String phone = weightDatabase.getPhoneNumber(USER_ID);
        if  (phone == null ) {
            check = false;
        }

        return check;
    }

    //Function to open PhoneNumberActivity and pass USER ID
    private void openPhoneNumberActivity(int ID) {
        Intent intent = new Intent(this, PhoneNumberActivity.class);
        intent.putExtra("USER_ID", ID);
        startActivity(intent);
    }


    //Open DataActivity
    private void openDataActivity(int ID) {
        Intent intent = new Intent(this, DataActivity.class);
        intent.putExtra("USER_ID", ID);
        intent.putExtra("DATE", date);
        startActivity(intent);
    }

    //Open Goal WeightActivity
    private void openGoalWeightActivity(int ID) {
        Intent intent = new Intent(this, GoalWeightActivity.class);
        intent.putExtra("USER_ID", ID);
        startActivity(intent);
    }

    //Open CalorieGoalActivity
    private void openCalorieGoalActivity(int ID) {
        Intent intent = new Intent(this, CalorieGoalActivity.class);
        intent.putExtra("USER_ID", ID);
        startActivity(intent);
    }

    private void openCalorieDataActivity(int ID) {
        Intent intent = new Intent(this, CalorieDataActivity.class);
        intent.putExtra("USER_ID", ID);
        intent.putExtra("DATE", date);
        startActivity(intent);
    }


    //Function for when notification button is pressed
    public void onNotificationButtonClick(View view) {
        //if no permissions
        if (!checkPermission()) {
            //request permission
            requestPermission();
        }
    }
    private boolean checkPermission() {
        // Check if sms permission is granted
        return ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        // Request the permission
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.SEND_SMS},
                SMS_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    //Function to manage permission results
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, show message thanking user
                Toast.makeText(this, "Thank You For Granting SMS Permission.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, show a message notifying user they can grant permissions later
                Toast.makeText(this, "SMS Permission can be granted in the settings at any time.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}