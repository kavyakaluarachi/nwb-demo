package com.parse.starter;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.NotificationJobService;

import java.util.List;


public class Homepage extends MenuActivity implements PopupMenu.OnMenuItemClickListener {

    private NotificationManagerCompat notificationManager;


//    LocationManager locationManager;
//    LocationListener locationListener;

    public static final String TAG = "Homepage";
    public Context appContext;


    Location home = new Location(LocationManager.GPS_PROVIDER);

//    public void goHome(View view) {
//        Intent goHome = new Intent(getApplicationContext(), Homepage.class);
//        goHome.putExtra("user", ParseUser.getCurrentUser().getString("name"));
//        startActivity(goHome);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.logoutButton) {
//            ParseUser.logOut();
//            Intent intentLogout = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intentLogout);
//        }
//        return super.onOptionsItemSelected(item);
//    }


    public void goToNewPage(View view) {
        Intent intent = new Intent();
        if (view.getId() == R.id.newsButton) {
            intent = new Intent(getApplicationContext(), Newsfeed.class);
        }

        if (view.getId() == R.id.fileReportButton) {
            intent = new Intent(getApplicationContext(), FileReport.class);
        }

        if (view.getId() == R.id.myActivityButton) {
            intent = new Intent(getApplicationContext(), MyActivity.class);
        }

        if (view.getId() == R.id.myNeighbourhoodButton) {
            intent = new Intent(getApplicationContext(), MyNeighbourhood.class);
        }

        if (view.getId() == R.id.importantContactsButton) {
            intent = new Intent(getApplicationContext(), ImportantActivityPage.class);
        }
        startActivity(intent);
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        getSupportActionBar().hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Button newsButton = (Button) findViewById(R.id.newsButton);
        appContext = this;


        notificationManager =  NotificationManagerCompat.from(this);

        NotificationJobService.notificationManager =  NotificationManagerCompat.from(this);
        NotificationJobService.mContext = this;
//        notifications();


        Intent intent = getIntent();
        String name = intent.getStringExtra("user");
        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome, " + name);
        welcomeTextView.setTypeface(null, Typeface.BOLD);


        //THESE FUNCTIONS ARE FOR NOTIFICATION JOBS, SCHEDULE JOB.
//
//        scheduleJob();
        sendIncidentNotification();
        sendStatusUpdateNotification();


    }


//    public void scheduleJob() {
//        ComponentName componentName = new ComponentName(this, NotificationJobService.class);
//        JobInfo info = new JobInfo.Builder(123, componentName)
//                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .setPersisted(true)
//                .setPeriodic(15*60*1000)
//                .build();
//
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = scheduler.schedule(info);
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Log.d(TAG, "Job Scheduled");
//        }
//        else {
//            Log.d(TAG, "Job Scheduling Failed");
//        }
//
//    }
//
//    public void cancelJob() {
//        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
//        scheduler.cancel(123);
//        Log.d(TAG, "Job Cancelled");
//
//    }

    // Send Notification in background


    public void sendIncidentNotification() {
        // first, check if user is up to date w/ incident notifications

        final ParseQuery<ParseObject> pendingNotifs = ParseQuery.getQuery("PendingNotifications");
        pendingNotifs.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        pendingNotifs.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if ( e == null) {
                    if (objects.size()>0) {
                        for (ParseObject object : objects) {
                            String type = object.getString("type");

                            Intent activityIntent = new Intent(getApplicationContext(), MyNeighbourhood.class);
                            PendingIntent contentIntent = PendingIntent.getActivity(Homepage.this, 0,activityIntent, 0);

                            String message = "A new " + type + " incident has occurred in your neighbourhood. Check 'my Neighbourhood' to view details";
                            android.app.Notification notification = new NotificationCompat.Builder(appContext, StarterApplication.CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.notificationthumbnailresized)
                                    .setContentTitle("New " + type + " Incident")
                                    .setContentText(message)
                                    .setStyle(new NotificationCompat.BigTextStyle())
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .setContentIntent(contentIntent)
                                    .build();

                            notificationManager.notify(1, notification);
                            Log.i("NotificationIncident", "sent");

                            ParseUser.getCurrentUser().put("notifiedUpdated", "true");
                            try {
                                object.delete();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            ParseUser.getCurrentUser().saveInBackground();

                        }
                    }
                }
            }
        });


        if (ParseUser.getCurrentUser().getString("notifiedUpdated").matches("false")) {
            Log.i("Incident:", "Incident notification needs to be sent");
            // the user is NOT up to date, so query incidents



        }

        else {
            Log.i("Incident", "Incident notification not sending");
            String stat = ParseUser.getCurrentUser().getString("pending");
            if (stat != null){

            }
            else {

            }
        }
    }

    public void sendStatusUpdateNotification() {
        // query all incident reports

        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("notified", "false");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size()>0) {

                    Intent activityIntent = new Intent(getApplicationContext(), MyActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0,activityIntent, 0);

                    String message = "An incident report you have submitted has  been reviewed. Go to 'My Activity' to view update.";
                    android.app.Notification notification = new NotificationCompat.Builder(appContext, StarterApplication.CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.notificationthumbnailresized)
                            .setContentTitle("Status Update")
                            .setContentText(message)
                            .setStyle(new NotificationCompat.BigTextStyle())
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                            .setContentIntent(contentIntent)
                            .build();

                    for (ParseObject object : objects) {
                        object.put("notified", "true");
                        object.saveInBackground();
                    }

                    notificationManager.notify(1, notification);
                    Log.i("Notification", "sent");

                }

                else {
                    Log.i("Notification", "Not sent");
                    if (e != null) {
                        Log.i("Error:", e.getMessage());
                    }


                }
            }
        });
    }





}
