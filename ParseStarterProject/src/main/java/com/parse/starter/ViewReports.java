package com.parse.starter;

import android.content.Intent;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewReports extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ListView reportsListView;
    ArrayList<String> reports = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ArrayList<String> references = new ArrayList<String>();


    public void viewClicked(View view) {
        if (view.getId() == R.id.pending) {
            updateListView("pending");
        }

        else if (view.getId() == R.id.approved) {
            updateListView("approved");
        }

        else {
            updateListView("declined");
        }

    }


    public void updateListView(final String status) {

        reports.clear();
        references.clear();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");

        query.whereEqualTo("status", status);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {

                if (e == null) {

                    if (objects.size()>0) {

                        for (ParseObject object: objects) {
                            String address = object.getString("address");
                            String dateSubmitted = object.getString("datesubmitted");
                            String incidentType = object.getString("incident");
                            String reference = object.getString("reference");
                            String reviewedBy = object.getString("reviewedby");

                            if (status == "approved" || status == "declined") {
                                reports.add("\n" + "Incident:" + incidentType + "\n" + "Location:" + address + "\n" + "Submitted: " + dateSubmitted + "\n" + "Reviewed by:" + reviewedBy +
                                        "\n" + "\n");
                                references.add(reference);

                            }

                            else if (status == "pending") {
                                reports.add("\n" + "Incident:" + incidentType + "\n" + "Location:" + address + "\n" + "Submitted: " + dateSubmitted
                                + "\n" + "\n");

                                references.add(reference);
                            }
                        }

                    }

                    else {
                        System.out.println("No objects!");
                        reports.add("No " + status + " incidents to show at this time.");
                    }

                    arrayAdapter.notifyDataSetChanged();
                }

                else {
                    System.out.println(e.getCause());
                }
            }
        });




    }

    // MENU STUFF

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    public void goHome(View view) {
        Intent goHome = new Intent(getApplicationContext(), HomepageAdmin.class);
        goHome.putExtra("user", ParseUser.getCurrentUser().getString("name"));
        startActivity(goHome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutButton) {
            ParseUser.logOut();
            Intent intentLogout = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentLogout);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.logoutButton) {
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(ViewReports.this, "You are logged out", Toast.LENGTH_SHORT).show();
                        startActivity(goToMain);
                    }
                }
            });
        }

        return true;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);


        reportsListView = (ListView) findViewById(R.id.listView);
        reports.add("Test");
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, reports);
        reportsListView.setAdapter(arrayAdapter);

        reports.clear();
        reports.add("Loading all reports...");

        updateListView("pending");

        getSupportActionBar().hide();

        reportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // get report info

                if (references.get(i) == null) {
                    System.out.println("null reference");
                }
                else {
                    System.out.println(references.get(i));

                    ParseQuery<ParseObject> query2 = ParseQuery.getQuery("IncidentReports");
                    query2.whereEqualTo("reference", references.get(i));

                    query2.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                if (objects.get(0) != null) {

                                    Intent intent = new Intent(getApplicationContext(), ViewIndividualReport.class);
                                    intent.putExtra("address", objects.get(0).getString("address"));
                                    intent.putExtra("description", objects.get(0).getString("description"));
                                    intent.putExtra("time", objects.get(0).getString("time"));
                                    intent.putExtra("reference", objects.get(0).getString("reference"));
                                    intent.putExtra("username", objects.get(0).getString("username"));
                                    intent.putExtra("incident", objects.get(0).getString("incident"));
                                    intent.putExtra("date", objects.get(0).getString("date"));
                                    intent.putExtra("status", objects.get(0).getString("status"));

                                    startActivity(intent);

                                }
                            }
                        }
                    });

                }
            }
        });

    }
}
