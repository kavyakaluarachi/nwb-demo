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

public class ViewIndividualReportUser extends MenuActivity {



    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.logoutButton) {
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(ViewIndividualReportUser.this, "You are logged out", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_view_individual_report_user);

        getSupportActionBar().hide();

        Intent intent = getIntent();

        TextView tv1 = findViewById(R.id.reftv1);
        tv1.setTypeface(null, Typeface.BOLD);
        TextView tv2 = findViewById(R.id.tv2);
        tv2.setTypeface(null, Typeface.BOLD);
        TextView tv3 = findViewById(R.id.adtv1);
        tv3.setTypeface(null, Typeface.BOLD);
        TextView tv4 = findViewById(R.id.desctv1);
        tv4.setTypeface(null, Typeface.BOLD);




        TextView username = findViewById(R.id.usernameTV1);
        TextView date = findViewById(R.id.statusTV);
        TextView address = findViewById(R.id.addressTV1);
        TextView description = findViewById(R.id.descriptionTV1);
        TextView type = findViewById(R.id.titleET);

        username.setText(intent.getStringExtra("name"));
        date.setText(intent.getStringExtra("when"));
        address.setText(intent.getStringExtra("address"));
        description.setText(intent.getStringExtra("description"));
        type.setText(intent.getStringExtra("type"));


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
