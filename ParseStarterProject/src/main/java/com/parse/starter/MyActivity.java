package com.parse.starter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

//import android.support.v7.view.menu.ActionMenuItemView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends MenuActivity {

    ParseUser user;
    ArrayList<Incident> myIncidents = new ArrayList<>();

    ListView activityListView;
    ArrayList<String> activities = new ArrayList<String>();
    ArrayAdapter arrayAdapter;
    ScrollView scrollView;

    public void updateListView() {
        activities.clear();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        query.whereEqualTo("username",user.getString("username"));
        query.orderByDescending("updatedAt");


        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size()>0) {
                        for (ParseObject object: objects) {

                            String type = object.getString("incident");
                            String date = object.getCreatedAt().toString();
                            String reference = object.getString("reference");
                            String address = object.getString("address");
                            String message = object.getString("description");
                            String status = object.getString("status");
                            String user = object.getString("username");
                            ParseFile image = object.getParseFile("imagepng");

                            Incident incident = new Incident(type,date, address, message, image, user, reference, status);
                            myIncidents.add(incident);

                        }
                    }


                    ActivityListAdapter adapter = new ActivityListAdapter(MyActivity.this, R.layout.custom_layout3, myIncidents);
                    activityListView.setAdapter(adapter);
                    scrollView.scrollTo(0,0);

                    activityListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getApplicationContext(), ViewActivity.class);

                            Incident incident2 =  myIncidents.get(i);

                            String status = incident2.getStatus();
                            String address = incident2.getLocation();
                            String description = incident2.getMessage();
                            String type = incident2.getType();
                            String reference = incident2.getReference();
                            String dateSubmitted = incident2.getWhen();

                            intent.putExtra("status", status);
                            intent.putExtra("address", address);
                            intent.putExtra("description", description);
                            intent.putExtra("type", type);
                            intent.putExtra("reference", reference);
                            intent.putExtra("dateSubmitted", dateSubmitted);

                            startActivity(intent);

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        getSupportActionBar().hide();

        scrollView = findViewById(R.id.scrollView2);
        user = ParseUser.getCurrentUser();

        activityListView = (ListView) findViewById(R.id.activityListView);

        updateListView();

    }
}
