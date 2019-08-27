package com.parse.starter;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class HomepageAdmin extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    Button messagesButton;
    Button reviewReportsButton;


    public void reviewReports(View view) {

        Intent intent = new Intent(getApplicationContext(), ViewReports.class);
        startActivity(intent);

    }




    // MENU STUFF

    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
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
                        Toast.makeText(HomepageAdmin.this, "You are logged out", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_homepage_admin);

        getSupportActionBar().hide();

        TextView welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);


        Intent intent = getIntent();
        String name = intent.getStringExtra("user");

        welcomeTextView.setText("Welcome, " + name);
    }
}
