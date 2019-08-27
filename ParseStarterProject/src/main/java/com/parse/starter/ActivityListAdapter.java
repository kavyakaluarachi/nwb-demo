package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class ActivityListAdapter extends ArrayAdapter<Incident> {


    private Context mContext;
    int mResource;

    public ActivityListAdapter(@NonNull Context context, int resource, @NonNull List<Incident> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String type = getItem(position).getType();
        String when = getItem(position).getWhen();
        String location = getItem(position).getLocation();
        String message = getItem(position).getMessage();
        String username = getItem(position).getUser();
        String reference = getItem(position).getReference();
        String statusTxt = getItem(position).getStatus();
        final ParseFile image = getItem(position).getImage();




        // create new event object with info
        Incident incident = new Incident(type, when, location, message, image, username, reference, statusTxt);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);


        final TextView status = (TextView) convertView.findViewById(R.id.statusTV);
        TextView reportType = (TextView) convertView.findViewById(R.id.reportTypeTV);
        TextView reportDate = (TextView) convertView.findViewById(R.id.submittedTV);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        query.whereEqualTo("reference", reference);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                status.setText(objects.get(0).getString("status"));
            }
        });


        status.setTypeface(null, Typeface.BOLD);
        reportType.setText(type + " Report");
        reportType.setTypeface(null, Typeface.BOLD);
        reportDate.setText(when);

        return convertView;
    }
}
