package com.parse.starter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class MyNeighbourhood extends MenuActivity {

    ListView incidentsList;
    ScrollView scrollView;

    ArrayList<Incident> incidents = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayList<String> incidentType = new ArrayList<>();

    Button listView;
    Button mapView;
    double homeLat;
    double homeLong;
    Incident incident = null;
    Location home = new Location(LocationManager.GPS_PROVIDER);


    public void buttonClick(View view) {
        if (view.getId() == R.id.listViewButton) {
            updateList("local");

        }

        if (view.getId() == R.id.mapViewButton) {
            Intent goMap = new Intent(getApplicationContext(), MapsActivity.class);
            goMap.putExtra("addresses", addresses);
            goMap.putExtra("incident", incidentType);
            startActivity(goMap);
        }
    }

    public void updateList(final String key) {
        incidents.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        System.out.println(key);
        query.whereEqualTo("status", "approved");

        final Geocoder geocoder = new Geocoder(this);
        try {
            // gets the user's address
            List<Address> list = geocoder.getFromLocationName(ParseUser.getCurrentUser().getString("address"), 1);
            Address address = list.get(0);
            homeLat = address.getLatitude();
            homeLong = address.getLongitude();

            home.setLatitude(homeLat);
            home.setLongitude(homeLong);

            System.out.println(Double.toString(homeLat) + "," + Double.toString(homeLong));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Sorry, error found!");
        }

        // query all approved incidents
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size()>0) {
                        // we only want to add distances which are less than <= 7000 away

                        for (ParseObject object : objects) {
                            try {

                                // check the address of the incident, and see distance
                                    List<Address> incidentAdd = geocoder.getFromLocationName(object.getString("address"), 1);
                                    Address add = incidentAdd.get(0);

                                    double lat = add.getLatitude();
                                    double lng = add.getLongitude();

                                    Location incidentLoc = new Location(LocationManager.GPS_PROVIDER);
                                    incidentLoc.setLatitude(lat);
                                    incidentLoc.setLongitude(lng);

                                    if (home.distanceTo(incidentLoc) <= 7000) {
                                        System.out.println("within neighbourhood");

                                        String type = object.getString("incident");
                                        String date = object.getString("date");
                                        String time = object.getString("time");
                                        String reference = object.getString("reference");
                                        String address = object.getString("address");
                                        String message = object.getString("description");
                                        String user = object.getString("username");
                                        ParseFile image = object.getParseFile("imagepng");
                                        String status = object.getString("status");


                                        String when = date + "  " + time;

                                        incident = new Incident(type,when, address, message, image, user, reference, status);
                                        addresses.add(address);
                                        incidents.add(incident);
//                                        incidents.add("\n" + "Incident:" + object.getString("incident") + "\n" +
//                                                "Location: " + object.getString("address") + "\n" +
//                                                "Date: " + object.getString("date") + "\n");
                                    }

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                System.out.println("Error in geocoder");
                                    System.out.println(e1.getMessage());
                                }
                            }


                        for (Incident incident : incidents) {
                           String incidentString =  incident.getType() + ":" + incident.getLocation() + ":" + incident.getReference() + ":" + incident.getMessage() + ":" + incident.getWhen();
                           incidentType.add(incidentString);
                        }

                        IncidentListAdapter adapter = new IncidentListAdapter(MyNeighbourhood.this, R.layout.custom_layout2, incidents);
                        incidentsList.setAdapter(adapter);

//                        arrayAdapter.notifyDataSetChanged();
                    }


                }

                else {
                    Log.i("myNeighbourhood", "error found");
                }

                scrollView.scrollTo(0,0);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_neighbourhood);
        getSupportActionBar().hide();

        scrollView = findViewById(R.id.scrollView1);


        Button mapView = findViewById(R.id.mapViewButton);
        Button listView = findViewById(R.id.listViewButton);


        incidentsList = (ListView) findViewById(R.id.listView);

        incidentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Incident incident = incidents.get(i);
                String address = incident.getLocation();
                String type = incident.getType();
                String message = incident.getMessage();
                String when = incident.getWhen();
                String name = incident.getUser();
                String reference = incident.getReference();

                Intent intent = new Intent(getApplicationContext(), ViewIndividualReportUser.class);
                intent.putExtra("address", address);
                intent.putExtra("type", type);
                intent.putExtra("description", message);
                intent.putExtra("when", when);
                intent.putExtra("name", name);
                intent.putExtra("reference",reference );

                startActivity(intent);

            }
        });
//        incidents.add("Test");
//        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, incidents);
//        incidentsList.setAdapter(arrayAdapter);
//
//        incidents.clear();
//        incidents.add("Loading all incidents...");
//
        updateList("local");



    }
}
