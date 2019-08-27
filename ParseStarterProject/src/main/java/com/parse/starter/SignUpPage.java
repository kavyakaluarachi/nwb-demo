package com.parse.starter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpPage extends AppCompatActivity {

    EditText nameEditText;
    EditText passwordEditText1;
    EditText passwordEditText2;
    EditText emailEditText;
    EditText addressEditText;
    EditText usernameEditText;

    public void backToLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    public void signUp(View view) {
        // check to see if any of the fields are left blank
        if (nameEditText.getText().toString().matches("") || passwordEditText1.getText().toString().matches("") ||
                passwordEditText2.getText().toString().matches("") || emailEditText.getText().toString().matches("") ||
                addressEditText.getText().toString().matches("")  || usernameEditText.getText().toString().matches("")
        ) {
            Toast.makeText(SignUpPage.this, "Please ensure all fields are complete", Toast.LENGTH_SHORT).show();
        }
        // if nothing is left blank, proceed
        else {
            // check to see that password fields match
            if (!passwordEditText1.getText().toString().matches(passwordEditText2.getText().toString())) {
                Toast.makeText(SignUpPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            // if match, proceed with sign up
            else {
                ParseUser user = new ParseUser();
                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText1.getText().toString());
                user.setEmail(emailEditText.getText().toString());
                user.put("name", nameEditText.getText().toString());
                user.put("address", addressEditText.getText().toString());

                // sign up
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Sign up", "Successful!");
                        } else {
                            Log.i("Sign up", "failed!");
                            Toast.makeText(SignUpPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Intent goToLogin = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(goToLogin);


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);
        getSupportActionBar().hide();

        setTitle("Create Account");

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        addressEditText = (EditText) findViewById(R.id.addressEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText1 = (EditText) findViewById(R.id.passwordEditText1);
        passwordEditText2 = (EditText) findViewById(R.id.passwordEditText2);
    }


}
