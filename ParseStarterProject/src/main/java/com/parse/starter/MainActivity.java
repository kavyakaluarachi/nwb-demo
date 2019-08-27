/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
  Boolean signUpModeActive = false;
  TextView createAccountText;
  EditText passwordTextField;
  EditText usernameTextField;
  ConstraintLayout backgroundRelativeLayout;
  CheckBox adminCheckBox;
  String userType;
  TextView welcomeText;

  public void goToSignup(View view) {
    Log.i("Changing Mode", "Sign Up Mode");
    signUpModeActive = true;
    Intent intent = new Intent(getApplicationContext(), SignUpPage.class);
    startActivity(intent);
  }


  // LOGGING INTO SYSTEM
  public void signIn(View view) {

    if (!signUpModeActive) {
      // we are logging in
      ParseUser.logInInBackground(usernameTextField.getText().toString(), passwordTextField.getText().toString(), new LogInCallback() {
        @Override
        public void done(ParseUser user, ParseException e) {
          // check if user exists
          if (user != null) {
            Log.i("Sign in:", "Successful");

            // if admin, redirect to admin homepage
            if (adminCheckBox.isChecked()) {
              System.out.println("Admin is checked");
              userType = "admin";
              Intent adminIntent = new Intent(getApplicationContext(), HomepageAdmin.class);
              adminIntent.putExtra("user", user.getString("name"));
              startActivity(adminIntent);
            } else {
              // else move to regular user homepage
              userType = "user";
              Intent homeIntent = new Intent(getApplicationContext(), Homepage.class);
              homeIntent.putExtra("user", user.getString("name"));
              startActivity(homeIntent);
            }

          }

          else {
            Log.i("Sign in:", "failed");
            System.out.println(e.getMessage());
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
          }
        }
      });
    }
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.relativeLayout) {
      InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }
  }





  @Override
  protected void onCreate(Bundle savedInstanceState) {


    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getSupportActionBar().hide();
    setTitle("Neighbourhood Watch Brampton");


    welcomeText = (TextView) findViewById(R.id.welcomeText);
    welcomeText.setTypeface(null, Typeface.BOLD);

    adminCheckBox = (CheckBox) findViewById(R.id.checkBox);
    passwordTextField = (EditText) findViewById(R.id.passwordTextField);
    createAccountText = (TextView) findViewById(R.id.createAccountText);
    usernameTextField = (EditText) findViewById(R.id.usernameTextField);
    backgroundRelativeLayout = (ConstraintLayout) findViewById(R.id.relativeLayout);
    backgroundRelativeLayout.setOnClickListener(this);

    if (ParseUser.getCurrentUser() != null) {
      System.out.println(ParseUser.getCurrentUser().getUsername());
    }

    ParseAnalytics.trackAppOpenedInBackground(getIntent());

  }



}
