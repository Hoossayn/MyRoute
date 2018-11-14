package com.example.android.myroute;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class map_history extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    public SimpleCursorAdapter simpleCursorAdapter;

    GoogleApiClient mGoogleApiClient;
    Location lastKnownLocation;
    Polyline line;
    private LocationManager locationManager;
    public String arrayValue;
    double latit;
    double longit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_history);


        MarkerPoints = new ArrayList<>();
        //arrayValue = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lastKnownLocation = locationManager.getLastKnownLocation(provider);




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       // arrayValue = new ArrayList<>();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        arrayValue = getIntent().getStringExtra("idValue");


        String[] ary = arrayValue.split(",");

        //LatLng point = new LatLng(latit, longit);
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

        for (int a = 0; a < ary.length; a++) {

            String[] finalsplit = Arrays.toString(ary).split(",");
            String[] anotherfinalsplit = Arrays.toString(new String[]{finalsplit[a]}).split("&");

            Log.d("Individual Items", Arrays.toString(new String[]{finalsplit[a]}));

            try{

                String latString = anotherfinalsplit[0].replace("[", "");
                String longString = anotherfinalsplit[1].replace("]", "");


                latit = Double.parseDouble(latString);
                longit = Double.parseDouble(longString);

                Log.d("aryvalue0", String.valueOf(latit));
                Log.d("aryvalue1", String.valueOf(longit));


                options.add(new LatLng(latit,longit));
                line = mMap.addPolyline(options); //add Polyline
                Log.d("polyline", String.valueOf(line));


               // Log.d("Coordinates", String.valueOf(point));
                Log.d("arrayLength", String.valueOf(ary.length));

                LatLng startpoint = new LatLng(latit, longit);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }
            catch (Exception e){e.printStackTrace();}

        }



    }
}

