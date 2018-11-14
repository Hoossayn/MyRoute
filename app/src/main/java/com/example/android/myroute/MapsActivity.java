package com.example.android.myroute;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.common.collect.MapMaker;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    public String descriptionValue;
    public double lng;
    public double lat;
    Dialog dialog;
    GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    public ArrayList<String> latlngArray;
    ArrayList<Routes> routeslist;
    GoogleApiClient mGoogleApiClient;
    Location lastKnownLocation;
    Location location;
    static LocationRequest mLocationRequest;
    MyLocationListener locationListener;
    public LocationManager locationManager;

    Polyline line;
    DbHelper db;
    double mLatitude = 0;
    double mLongitude = 0;
    ListView listView;
    Button btn_start;
    Button btn_stop;
    Button history;
    TextView routeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startService(new Intent(this, MyService.class));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<LatLng>();
        latlngArray = new ArrayList<String>();
        routeslist = new ArrayList<>();


        db = new DbHelper(this);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setEnabled(false);
        history = (Button) findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteList.class);
                startActivity(intent);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }

        final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
   /*     criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);*/

        //API level 9 and up
       /* criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);*/

        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastKnownLocation = locationManager.getLastKnownLocation(provider);

        Location location = locationManager.getLastKnownLocation(provider);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Googl//   addMarker(); //add Marker in current positione Play services and returned to the app.
         */


    }

    @Override
    protected void onPause() {
        super.onPause();
        // redrawLine();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);

                if (lastKnownLocation != null) {
                    mLatitude = lastKnownLocation.getLatitude();
                    mLongitude = lastKnownLocation.getLongitude();

                    LatLng point = new LatLng(mLatitude, mLongitude);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
//        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, (float) (0.5), new LocationListener() {
//                @Override
//                public void onLocationChanged(Location location) {
//
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    LatLng latLng = new LatLng(latitude, longitude); //you already have this
//
//                    MarkerPoints.add(latLng); //added
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//                    redrawLine(); //added
//                }
//
//                @Override
//                public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                }
//
//                @Override
//                public void onProviderEnabled(String provider) {
//
//                }
//
//                @Override
//                public void onProviderDisabled(String provider) {
//
//                }
//            });
//        }

        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude); //you already have this

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


        }

        redrawLine();
    }

    private void redrawLine() {

        if (mMap != null) {
            mMap.clear();  //clears all Markers and Polylines
        }
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE);
        for (int i = 0; i < MarkerPoints.size(); i++) {
            LatLng latLng = MarkerPoints.get(i);
            options.add(latLng);

            Log.d("redrawline", String.valueOf(latLng));
        }


             //add Marker in current position


        line = mMap.addPolyline(options); //add Polyline
        Log.d("polyline", String.valueOf(MarkerPoints));
    }


    private void CordSaver() {
        mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < MarkerPoints.size(); i++) {
            LatLng point = MarkerPoints.get(i);
            options.add(point);
            if (lastKnownLocation != null) {
                lng = lastKnownLocation.getLatitude();
                lat = lastKnownLocation.getLongitude();
                LatLng s = new LatLng(lng, lat);

                //  latlngArray.add(s);
            }
            //LatLng lastpoint = lat+"&"+lng;

            Log.d("Latlng Array", String.valueOf(latlngArray));
        }


        line = mMap.addPolyline(options); //add Polyline
    }


    public void routeSaver() {

        locationListener = new MyLocationListener();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000000, 1, locationListener);
        }
         String lastpoint = null;

        mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < MarkerPoints.size(); i++) {
            LatLng point = MarkerPoints.get(i);
            options.add(point);

            }

        }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            dialog = new Dialog(MapsActivity.this); // Context, this, etc.
            dialog.setContentView(R.layout.dialog_obtainer);
            Button okbtn = (Button) dialog.findViewById(R.id.okbtn);
            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelbtn);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText description = (EditText) dialog.findViewById(R.id.description);
                    String descriptionCheck = description.getText().toString();
                    if(!descriptionCheck.isEmpty()){
                        descriptionValue = Objects.toString(description.getText().toString(), "");
                        //CordSaver();
                        redrawLine();
                        routeSaver();
                        dialog.cancel();
                        btn_start.setEnabled(false);
                        btn_stop.setEnabled(true);
                    }
                    else {
                        Toast.makeText(MapsActivity.this,"Please enter some Text first",Toast.LENGTH_LONG).show();
                    }
                }
            });
            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        } else if (v.getId() == R.id.btn_stop) {
            Log.d("Stop", "Stop Button is being triggered");
            mMap.clear();
            //Write code here to position the pointer at the current position specified by the location manager

            SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat timeformatter = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date();
            String dateValue = dateformatter.format(date);
            String timeValue = timeformatter.format(date);
            String descriptionInitial = descriptionValue.substring(0,1);
            db.insertData(descriptionValue,latlngArray, dateValue, timeValue, descriptionInitial);
            Intent intent = new Intent(getBaseContext(), RouteList.class);
            intent.putExtra("LAT_LNG_ARRAY", latlngArray);
            //startActivity(intent);
            btn_stop.setEnabled(false);
            btn_start.setEnabled(true);
            Log.d("Arraysizelatlng",String.valueOf(latlngArray.size()));


        }
    }


    public class MyLocationListener implements LocationListener {

        String lastpoint = null;
        public void onLocationChanged( Location loc) {


      //  Bind to the service and get update from service



            double latitude =  loc.getLatitude();
            double longitude = loc.getLongitude();

            double lati =  loc.getLatitude();
            double longi= loc.getLongitude();


            LatLng latLng = new LatLng(latitude, longitude); //you already have this

            MarkerPoints.add(latLng); //added

            Location location = new Location(LocationManager.GPS_PROVIDER);

            double lng = location.getLongitude();
            double lat = location.getLatitude();

            double difference_in_locations = loc.distanceTo(location);
            Log.d("difference in location", String.valueOf(difference_in_locations));

            Log.d("LatLngOnLocationChanged", String.valueOf(latLng));

            if (difference_in_locations >= 1296120.0) {

                String lastpoint = lati + "&" + longi;

                Log.d("Latlng lastPoint", String.valueOf(lastpoint));

                latlngArray.add(lastpoint);
                routeSaver();
                redrawLine(); //added

            }

        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


    }

}