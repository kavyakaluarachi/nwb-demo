package com.parse.starter;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public abstract class NotificationAbstract extends AppCompatActivity {


    public NotificationManagerCompat notificationManager;
    public Context mContext;


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
                            PendingIntent contentIntent = PendingIntent.getActivity(NotificationAbstract.this, 0,activityIntent, 0);

                            String message = "A new " + type + " incident has occurred in your neighbourhood. Check 'my Neighbourhood' to view details";
                            android.app.Notification notification = new NotificationCompat.Builder(mContext, StarterApplication.CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.unnamed)
                                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                                            R.drawable.unnamed))
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
                    android.app.Notification notification = new NotificationCompat.Builder(mContext, StarterApplication.CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.unnamed)
                            .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.unnamed))
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
