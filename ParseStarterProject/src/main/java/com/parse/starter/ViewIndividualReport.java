package com.parse.starter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewIndividualReport extends AppCompatActivity {
    TextView usernameEditText;
    TextView descriptionEditText;
    TextView referenceEditText;
    TextView dateSubmitted;
    TextView addressEditText;
    TextView typeEditText;
    TextView dateEditText;
    TextView currentStatus;
    ArrayList<String> notifyUsers = new ArrayList<>();
    ImageView incidentImageView;
    Location incidentLoc = new Location(LocationManager.GPS_PROVIDER);
    Geocoder geocoder;
    Intent intent;

    Button decline;
    Button approve;



    //!!!!!!!!!! IMPROVE EFFICIENCY O(N^2)

    public void onButtonClickDecision(final View view) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        query.whereEqualTo("reference", intent.getStringExtra("reference"));
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size()>0) {

                        //if we clicked, decline -> update status to decline
                        if (view.getId() == R.id.decline) {
                            objects.get(0).put("status", "declined");
                        }
                        // if we clicked approve -> update status to approve
                        else if (view.getId() == R.id.approve) {
                            objects.get(0).put("status", "approved");

                            ParseQuery<ParseUser> userQ = ParseUser.getQuery();
                            userQ.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(List<ParseUser> usersQ, ParseException e) {

                                    for (ParseUser user : usersQ) {
                                        String username = user.getUsername();
                                        String address = user.getString("address");

                                        try {
                                            List<Address> list = geocoder.getFromLocationName(address, 1);
                                            Address add = list.get(0);

                                            Location home = new Location(LocationManager.GPS_PROVIDER);
                                            home.setLatitude(add.getLatitude());
                                            home.setLongitude(add.getLongitude());

                                            if (home.distanceTo(incidentLoc) <= 7000) {
                                                notifyUsers.add(username);
                                            }
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    System.out.println(notifyUsers);


                                    for (int i=0; i < notifyUsers.size(); i++) {
                                        ParseObject pendingNotifications = new ParseObject("PendingNotifications");
                                        pendingNotifications.put("username", notifyUsers.get(i));
                                        pendingNotifications.put("type", typeEditText.getText());
                                        pendingNotifications.saveInBackground();
                                    }

                                }
                            });



                        }

                        // NOTIFY USER WHO POSTED
                        objects.get(0).put("notified", "false");

                        objects.get(0).put("reviewedby", ParseUser.getCurrentUser().getString("username"));
                        objects.get(0).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    System.out.println("Saving status updates");
                                    currentStatus.setText(objects.get(0).getString("status"));
                                    Toast.makeText(ViewIndividualReport.this, "Status successfuly changed", Toast.LENGTH_SHORT).show();
                                } else {
                                    System.out.println("Failed to save");
                                    Toast.makeText(ViewIndividualReport.this, "Could not update status at this time. Try again later.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                }
            }
        });
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_individual_report);
        getSupportActionBar().hide();


        dateSubmitted = (TextView) findViewById(R.id.dateSubmittedET);

        usernameEditText = (TextView) findViewById(R.id.usernameET);
        usernameEditText.setTypeface(null, Typeface.BOLD);

        addressEditText = (TextView) findViewById(R.id.addressET);

        typeEditText = (TextView) findViewById(R.id.incidentET);
        descriptionEditText = (TextView) findViewById(R.id.descriptionET);

        referenceEditText = (TextView) findViewById(R.id.referenceET);


        dateEditText = (TextView) findViewById(R.id.dateIncidentET);

        incidentImageView = (ImageView) findViewById(R.id.imageET);

        intent = getIntent();
        usernameEditText.setText(intent.getStringExtra("username"));
        addressEditText.setText( intent.getStringExtra("address"));
        typeEditText.setText(intent.getStringExtra("incident"));
        descriptionEditText.setText(intent.getStringExtra("description"));
        referenceEditText.setText(intent.getStringExtra("reference"));

        dateEditText.setText( intent.getStringExtra("date") + ", " + intent.getStringExtra("time"));


        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        query.whereEqualTo("reference", intent.getStringExtra("reference"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    dateSubmitted.setText(objects.get(0).getCreatedAt().toString());

                    if (objects.get(0).getParseFile("imagepng") != null) {
                        ParseFile image = objects.get(0).getParseFile("imagepng");
                        image.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    incidentImageView.setImageBitmap(bitmap);
                                }
                            }
                        });
                    }
                }
            }
        });


        currentStatus = (TextView) findViewById(R.id.statusET);
        currentStatus.setText(intent.getStringExtra("status"));
        currentStatus.setTypeface(null, Typeface.BOLD);


        decline = (Button) findViewById(R.id.decline);
        approve = (Button) findViewById(R.id.approve);


        // set up geocoder
        geocoder = new Geocoder(ViewIndividualReport.this);
        try {
            List<Address> listIncident = geocoder.getFromLocationName(addressEditText.getText().toString(), 1);
            Address addressIncident = listIncident.get(0);
            incidentLoc.setLatitude(addressIncident.getLatitude());
            incidentLoc.setLongitude(addressIncident.getLongitude());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
