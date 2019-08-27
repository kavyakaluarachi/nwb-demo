package com.parse.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileReport extends MenuActivity {

    Spinner incidentTypeSpinner;
    ParseUser user;
    ParseFile image;
    EditText addressIncident;
    EditText descriptionIncident;
    EditText referenceIncident;
    EditText timeIncident;
    String incidentType;
    EditText dateIncident;
    String currentDate;
    public static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // when user selects an image from a gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            // make sure the user opened gallery, used gallery intent, image picked

            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                image = new ParseFile("image.png", byteArray);


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


    public void uploadIncidentPhoto(View view) {
        // first, check permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // if we dont have permission, request permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

            else {
                // we have permission, so start gallery intent
                selectPhoto();
            }
        }

        else {
            selectPhoto();
        }
    }

    public void selectPhoto() {

        if (referenceIncident.getText().toString() == "") {
            // alert
            new AlertDialog.Builder(FileReport.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Complete All Required Fields")
                    .setMessage("Please ensure that all fields are completed before uploading photo.")
                    .setPositiveButton("Close", null)
                    .show();
        } else {

            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, RESULT_LOAD_IMAGE);


        }

    }


    // submitting the report + saving it onto parse
    public void submitReport(View view) {

        if (addressIncident.getText().toString().matches("") ||
                descriptionIncident.getText().toString().matches("")||
                referenceIncident.getText().toString().matches("") ||
                timeIncident.getText().toString().matches("")){

            Toast.makeText(FileReport.this, "Please ensure all fields are complete before submitting", Toast.LENGTH_SHORT).show();

        }

        else {
            String address = addressIncident.getText().toString();
            String description = descriptionIncident.getText().toString();
            String reference = referenceIncident.getText().toString();
            String time = timeIncident.getText().toString();
            String date = dateIncident.getText().toString();

            ParseObject incidentReports = new ParseObject("IncidentReports");
            incidentReports.put("address", address);
            incidentReports.put("description", description);
            incidentReports.put("date", date);
            incidentReports.put("reference", reference);
            incidentReports.put("time", time);
            incidentReports.put("username", user.getString("username"));
            incidentReports.put("incident", incidentType);
            incidentReports.put("datesubmitted", currentDate);

            if (image != null) {
                incidentReports.put("imagepng", image);
            }

            System.out.println("lineImage");
            incidentReports.put("status", "pending");
            System.out.println("lineStatus");

            incidentReports.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Save Report", "Successful");
                        Toast.makeText(FileReport.this, "Successfully submitted incident report.", Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(FileReport.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Report Submitted")
                                .setMessage("Thank you for reporting this incident. We will review it as soon as we can and may contact you for additional information.")
                                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        backToHomepage();
                                    }
                                })
                                .setNegativeButton("Submit Another Report", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                }).show();

                    }
                    else {
                        System.out.println("line3");
                        Log.i("Save Report", "Failed");
                        Toast.makeText(FileReport.this, "Unable to report incident. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            System.out.println("line4");

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_report);
        getSupportActionBar().hide();

        // get date
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        currentDate = df.format(date);


        // initialize user
        user = ParseUser.getCurrentUser();

        // initialize widgets
        addressIncident = (EditText) findViewById(R.id.usernameEditText);
        descriptionIncident = (EditText) findViewById(R.id.descriptionIncident);
        referenceIncident = (EditText) findViewById(R.id.referenceIncident);
        timeIncident = (EditText) findViewById(R.id.timeIncident);
        dateIncident = (EditText) findViewById(R.id.dateIncident);
        Button submitButton = (Button) findViewById(R.id.uploadPhotoIncident);

        ConstraintLayout backgroundView = (ConstraintLayout) findViewById(R.id.constraintLayoutFR);

        // set up spinner/drop down menu
        incidentTypeSpinner = (Spinner) findViewById(R.id.incidentTypeSpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(FileReport.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.incidents));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incidentTypeSpinner.setAdapter(arrayAdapter);

        incidentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                incidentType = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                incidentType="";
            }
        });


        // create alert

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Important Information")
                .setMessage("Reporting an incident is to be used for non-emergency uses only. If your incident requires emergency attention, " +
                        "please call 9-1-1 first. For municipal services, please call 3-1-1 first.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // when continue is clicked
                        Toast.makeText(FileReport.this, "Continue to report incident", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel is selected, return to homepage
                      backToHomepage();
                    }
                }).show();



        ParseAnalytics.trackAppOpenedInBackground(getIntent());

    }




    public void backToHomepage() {
        Intent homeIntent = new Intent(getApplicationContext(), Homepage.class);
        homeIntent.putExtra("user", user.getString("name"));
        startActivity(homeIntent);
    }


}
