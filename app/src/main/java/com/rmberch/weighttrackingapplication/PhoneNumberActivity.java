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

//Code for PhoneNumberActivity
public class PhoneNumberActivity extends AppCompatActivity {
    //Variable declaration
    int USER_ID;
    EditText phone;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        //Get user id from previous activity
        Intent intent = getIntent();
        USER_ID = intent.getIntExtra("USER_ID", -1);

        //Bind layout elements
        phone = findViewById(R.id.phoneEditText);
        submit = findViewById(R.id.submitPhoneButton);

        //When phone has text, updateButtonState
        phone.addTextChangedListener(new TextWatcher() {
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

        //When submit clicked
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get phonenumber as string
                String phoneText = phone.getText().toString();
                Boolean success = false;
                //If invalid length
                if (phoneText.length() != 10) {
                    //error message
                    Toast.makeText(PhoneNumberActivity.this, "Incorrect Number Format. Numbers only.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Else add to database
                    WeightDatabase weightDatabase = new WeightDatabase(PhoneNumberActivity.this);
                    success = weightDatabase.setPhoneNumber(phoneText, USER_ID);
                }
                if (success) {
                    //If successful, success message
                    Toast.makeText(PhoneNumberActivity.this, "Phone number successfully saved.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //else error message
                    Toast.makeText(PhoneNumberActivity.this, "Error saving number. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //Update button if text
    private void updateButtonState() {
        if (phone != null) {
            submit.setEnabled(!phone.getText().toString().isEmpty());
        }
    }

    //Close activity
    public void onBackButtonClick(View view) { finish(); }
}