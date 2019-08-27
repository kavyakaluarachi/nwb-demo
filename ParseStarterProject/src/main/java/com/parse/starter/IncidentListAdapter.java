package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class IncidentListAdapter extends ArrayAdapter<Incident> {

    private Context mContext;
    int mResource;


    public IncidentListAdapter(@NonNull Context context, int resource, @NonNull List<Incident> objects) {
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
        String status = getItem(position).getStatus();
        final ParseFile image = getItem(position).getImage();



        // create new event object with info
        Incident incident = new Incident(type, when, location, message, image, username, reference, status);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        final ImageView incidentPhoto = (ImageView) convertView.findViewById(R.id.reportPhoto);
        TextView incidentType = (TextView) convertView.findViewById(R.id.reportTypeTV);
        TextView incidentAddress = (TextView) convertView.findViewById(R.id.addressInfo);
        TextView incidentMessage = (TextView) convertView.findViewById(R.id.submittedTV);
        TextView dateInfo = convertView.findViewById(R.id.dateInfo);

        incidentType.setText(type);
        incidentType.setTypeface(null, Typeface.BOLD);
        incidentMessage.setText(message);
        incidentAddress.setText(location);
        dateInfo.setText(when);
        dateInfo.setTypeface(null, Typeface.BOLD);

        if (image != null) {
            image.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null && data != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        incidentPhoto.setImageBitmap(bitmap);
                    }
                }
            });
        }


        return convertView;
    }
}
