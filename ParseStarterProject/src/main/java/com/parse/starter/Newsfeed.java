package com.parse.starter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Newsfeed extends MenuActivity {

    public void onArticleClick(View view) {
        String url = "";

        if (view.getId() == R.id.riyaImage) {
            url = "https://nwbrampton.ca/canadas-remarkable-generosity-in-memory-of-riya/";
        }


        if (view.getId() == R.id.haltonCrimeImage) {
            url = "https://nwbrampton.ca/halton-police-smash-organized-gta-break-and-enter-operation/";
        }

        if (view.getId() == R.id.contactImageView) {
            url = "https://nwbrampton.ca/school-bus-mounted-cameras-admissible-as-evidence/";
        }

        Intent intent = new Intent(getApplicationContext(), ArticleView.class);
        intent.putExtra("article", url);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        getSupportActionBar().hide();
    }
}
