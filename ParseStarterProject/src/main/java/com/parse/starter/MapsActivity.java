package com.parse.starter;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    ArrayList<Address> addresses = new ArrayList<>();
    ArrayList<String> incidentsInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        setTitle("Map View");
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        Intent intent = getIntent();
        incidentsInfo = intent.getStringArrayListExtra("incident");

        Geocoder geocoder = new Geocoder(this);
        int i = 0;

        for (String add : incidentsInfo) {
            try {
                // split the string
                String incidentInfo = incidentsInfo.get(i);
                String[] separated = incidentInfo.split(":");
                String type = separated[0];
                String address1 = separated[1];
                String reference = separated[2];
                String message = separated[3];
                String when = separated[4];

                mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

                // fetch the address of incident, add it to the list
//                List<Address> list = geocoder.getFromLocationName(intent.getStringArrayListExtra("addresses").get(i), 1);

                List<Address> list = geocoder.getFromLocationName(address1, 1);
                Address address = list.get(0);
                addresses.add(address);
                //convert it to latlng
                LatLng loc = new LatLng(address.getLatitude(), address.getLongitude());
                //add a marker
                mMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .title(type)
                        .snippet("Address: " + address1 + "\n" +
                                "Date: " + when + "\n" +
                                "Message: " + message));

            } catch (IOException e) {

                e.printStackTrace();
            }

            i++;
            System.out.println("i = " + i);
            System.out.println("addresses are: "+  addresses.size());

        }
        // marks the users house

        String userAddress = ParseUser.getCurrentUser().getString("address");
        try {
           List<Address> userList = geocoder.getFromLocationName(userAddress, 1);
           Address userAdd = userList.get(0);
           LatLng userLatLng = new LatLng(userAdd.getLatitude(), userAdd.getLongitude());
           mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Home")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14));

        } catch (IOException e) {
            e.printStackTrace();
        }




    }


}
