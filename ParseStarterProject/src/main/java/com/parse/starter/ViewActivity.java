package com.parse.starter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ViewActivity extends MenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        TextView randomtv1 = findViewById(R.id.statusTV5);
        randomtv1.setTypeface(null, Typeface.BOLD);
        TextView randomtv2 = findViewById(R.id.dateIncidentTV);
        randomtv2.setTypeface(null, Typeface.BOLD);

        TextView desctv1 = findViewById(R.id.desctv1);
        desctv1.setTypeface(null, Typeface.BOLD);
        TextView reftv1 = findViewById(R.id.reftv1);
        reftv1.setTypeface(null, Typeface.BOLD);
        TextView adtv1 = findViewById(R.id.adtv1);
        adtv1.setTypeface(null, Typeface.BOLD);


        TextView status = findViewById(R.id.incidentET);
        status.setTypeface(null, Typeface.BOLD);
        status.setText(intent.getStringExtra("status"));

        TextView address = findViewById(R.id.addressET);
        address.setText(intent.getStringExtra("address"));

        TextView description = findViewById(R.id.descriptionET);
        description.setText(intent.getStringExtra("description"));

        Button typeButton = (Button) findViewById(R.id.typeET);
        typeButton.setText(intent.getStringExtra("type") + " Report");

        TextView dateSubmitted = findViewById(R.id.dateIncidentET);
        dateSubmitted.setText(intent.getStringExtra("dateSubmitted"));


        TextView reference = findViewById(R.id.referenceET);
        reference.setText(intent.getStringExtra("reference"));




        ParseQuery<ParseObject> query = ParseQuery.getQuery("IncidentReports");
        query.whereEqualTo("reference", intent.getStringExtra("reference"));


        System.out.println(intent.getStringExtra("reference"));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if ( e == null) {
                    ParseFile image = objects.get(0).getParseFile("imagepng");
                    if (image != null) {
                        image.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    ImageView img = findViewById(R.id.imageET);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    img.setImageBitmap(bitmap);
                                }
                            }
                        });

                    }

                    else {
                        System.out.println("image is null");
                    }

                }

                else {
                    Log.i("Error", "found" );
                }
            }
        });

    }
}
