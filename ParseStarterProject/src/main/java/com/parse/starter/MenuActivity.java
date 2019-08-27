package com.parse.starter;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MenuActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public void goHome(View view) {
        Intent goHome = new Intent(getApplicationContext(), Homepage.class);
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


    public void showPopup(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.main_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.logoutButton) {
            ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
                        Toast.makeText(getApplicationContext(), "You are logged out", Toast.LENGTH_SHORT).show();
                        startActivity(goToMain);
                    }
                }
            });
        }

        if (menuItem.getItemId() == R.id.contactButton) {
            Intent contactIntent = new Intent(getApplicationContext(), ContactPageEmail.class);
            startActivity(contactIntent);
        }

        return true;
    }

}
