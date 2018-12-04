package com.example.android.myroute.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myroute.DbHelper.DbHelper;
import com.example.android.myroute.Haversine;
import com.example.android.myroute.MyService;
import com.example.android.myroute.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {
    private int LOCATION_PERMISSION_CODE = 1;
    public String descriptionValue;
    Dialog dialog;
    GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    public ArrayList<String> latlngArray;
    Location lastKnownLocation;
    MyLocationListener locationListener;
    public LocationManager locationManager;

    Polyline line;
    DbHelper db;
    double mLatitude = 0;
    double mLongitude = 0;
    Button btn_start;
    Button btn_stop;
    Button history;
    TextView distanceCovered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startService(new Intent(this, MyService.class));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //  checkLocationPermission();
        }
        requestStoragePermission();
        //AllowRunTimePermission();

        // Initializing
        MarkerPoints = new ArrayList<LatLng>();
        latlngArray = new ArrayList<String>();
        distanceCovered = (TextView)findViewById(R.id.displayDistanceCovered);


        db = new DbHelper(this);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop.setEnabled(false);
        history = (Button) findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

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
       routeSaver();
       redrawLine();
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

        if (lastKnownLocation != null) {
            double latitude = lastKnownLocation.getLatitude();
            double longitude = lastKnownLocation.getLongitude();

            LatLng latLng = new LatLng(latitude, longitude); //you already have this

            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                String localeAddress = addressList.get(0).getLocality() + ",";
                localeAddress += addressList.get(0).getCountryName();
                mMap.addMarker(new MarkerOptions().position(latLng).title(localeAddress));

                //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            }catch (Exception e){e.printStackTrace();}

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

            try{
            line = mMap.addPolyline(options); //add Polyline
            Log.d("polyline", String.valueOf(MarkerPoints));

            }catch (Exception e){e.printStackTrace();}
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

       // mMap.clear();  //clears all Markers and Polylines
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < MarkerPoints.size(); i++) {
            LatLng point = MarkerPoints.get(i);
            options.add(point);

            }

        }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            btn_start.setText("Recording");
            Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.record);
            btn_start.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
            redrawLine();
            routeSaver();

            btn_start.setEnabled(false);
            btn_stop.setEnabled(true);

        } else if (v.getId() == R.id.btn_stop) {

            btn_start.setText("Start");
            Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.stop);
            btn_start.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
            dialog = new Dialog(MapsActivity.this); // Context, this, etc.
            distanceCovered.setText("");
            dialog.setContentView(R.layout.dialog_obtainer);
            Button okbtn = (Button) dialog.findViewById(R.id.okbtn);
            Button cancelbtn = (Button) dialog.findViewById(R.id.cancelbtn);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMap.clear();
                    EditText description = (EditText) dialog.findViewById(R.id.description);
                    String descriptionCheck = description.getText().toString();

                    //Write code here to position the pointer at the current position specified by the location manager


                    descriptionValue = Objects.toString(description.getText().toString(), "");
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

                    dialog.dismiss();
                }
            });
            cancelbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn_start.setEnabled(true);
                    dialog.cancel();
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }
    }


    public class MyLocationListener implements LocationListener {

        String lastPoint = null;
        final int EarthRadius = 6371;
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

                lastPoint = lati + "&" + longi;

               Log.d("Latlng lastPoint", String.valueOf(lastPoint));

                latlngArray.add(lastPoint);
                routeSaver();
                redrawLine(); //added

            }


            mLatitude = lastKnownLocation.getLatitude();
            mLongitude = lastKnownLocation.getLongitude();
            lati = loc.getLatitude();
            longi = loc.getLongitude();


            double d = Haversine.distance(mLatitude,mLongitude,lati,longi);

            DecimalFormat formatter = new DecimalFormat("#0.00");
            //formatter.format(d);

         /*   switch (d){
                case d < 1000:

                    break;
                case d >=1000:
                    break;
                default:
                    break;
            }*/

            if(d < 1000){
                String ss = String.valueOf(formatter.format(d));
                distanceCovered.setText(ss +"m");
            }else if(d >= 1000){
                String ss = String.valueOf(formatter.format(d));
                distanceCovered.setText(ss +"km");
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

    private void requestStoragePermission() {

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }if(ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            {
                ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        else{
            // Write you code here if permission already given.

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


}