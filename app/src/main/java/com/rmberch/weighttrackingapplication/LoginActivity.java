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

// LoginActivity.java
public class LoginActivity extends AppCompatActivity {
    //Variable declaration
    Button loginButton, createButton;
    EditText usernameEdit, passwordEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Bind Buttons
        loginButton = findViewById(R.id.logInButton);
        createButton = findViewById(R.id.createAccountButton);
        usernameEdit = findViewById(R.id.usernameEditText);
        passwordEdit = findViewById(R.id.passwordEditText);

        //Text changed listeners to only enable buttons when both fields have text
        usernameEdit.addTextChangedListener(new TextWatcher() {
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
        passwordEdit.addTextChangedListener(new TextWatcher() {
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


        //When login is clicked
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id;
                UserModel userModel;
                //Make user model
                try {
                    userModel = new UserModel(-1, usernameEdit.getText().toString(), passwordEdit.getText().toString());
                } catch (Exception e) {
                    //invalid userModel
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password. Try Again.", Toast.LENGTH_SHORT).show();
                    userModel = new UserModel(-1, "error", "error");
                }
                if (!userModel.getUsername().equals("error") && !userModel.getPassword().equals("error")) {
                    //if valid, call database login
                    WeightDatabase weightDatabase = new WeightDatabase(LoginActivity.this);
                    id = weightDatabase.logIn(userModel);
                    //if successful, login
                    if (id != -1) {
                        openHomeActivity(id);
                    }
                    else {
                        //Else error message
                        Toast.makeText(LoginActivity.this, "No account found. Check your username and password.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        //When create account clicked
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Variable declaration
                UserModel userModel;
                boolean success = false;
                int existingAccount;


                try {
                    //Try to get text and make userModel
                    userModel = new UserModel(-1, usernameEdit.getText().toString(), passwordEdit.getText().toString());
                } catch (Exception e) {
                    //Error, if failed
                    Toast.makeText(LoginActivity.this, "Invalid Username or Password. Try Again.", Toast.LENGTH_SHORT).show();
                    userModel = new UserModel(-1, "error", "error");
                }

                //If not invalid
                if (!userModel.getUsername().equals("error") && !userModel.getPassword().equals("error")) {
                    //Check if account exists
                    WeightDatabase weightDatabase = new WeightDatabase(LoginActivity.this);
                    existingAccount = weightDatabase.checkExisting(userModel);
                    //if it doesn't
                    if (existingAccount == -1) {
                        //add account to database
                        success = weightDatabase.addAccount(userModel);
                    }
                    else {
                        //if existing account, error message
                        Toast.makeText(LoginActivity.this, "An account with this Username already exists. Try a new Username.", Toast.LENGTH_LONG).show();
                    }
                    //If account added
                    if (success) {
                        //Get id from database and log in
                        int id = weightDatabase.logIn(userModel);
                        openHomeActivity(id);
                    }
                }
            }
        });
    }

    //Checks to enable buttons
    private void updateButtonState() {
        if (usernameEdit != null && passwordEdit != null) {
            loginButton.setEnabled(!usernameEdit.getText().toString().isEmpty() && !passwordEdit.getText().toString().isEmpty());
            createButton.setEnabled(!usernameEdit.getText().toString().isEmpty() && !passwordEdit.getText().toString().isEmpty());
        }
    }

    //Opens HomeActivity and passes USER ID
    private void openHomeActivity(int id) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra("USER_ID", id);
        startActivity(intent);
        finish(); // Close the login activity
    }
}
