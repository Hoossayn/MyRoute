package com.example.android.myroute;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
      /*  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener */ {

    GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    ArrayList<LatLng> latlngArray;
    GoogleApiClient mGoogleApiClient;
    Location lastKnownLocation;
    private LocationManager locationManager;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    // private ArrayList<LatLng> points; //added
    Polyline line;

    private static final LatLng IKEJA = new LatLng(6.605874, 3.349149);
    private static final LatLng OYO = new LatLng(7.536318, 3.418143);

    double mLatitude = 0;
    double mLongitude = 0;
    public  double lng;
    public double lat;

  /*  double latitude = mInitialLocation.getLatitude();
    double longitude = mInitialLocation.getLongitude();

    LatLng startLatLng = new LatLng(latitude, longitude);
    LatLng endLatLng = new LatLng(mLatitude, mLongitude);*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Initializing
        MarkerPoints = new ArrayList<LatLng>();

        latlngArray = new ArrayList<LatLng>();



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //  mMap.setMyLocationEnabled(true);

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);

        lastKnownLocation = locationManager.getLastKnownLocation(provider);


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */


        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, (float)(0.5), new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude); //you already have this

                    MarkerPoints.add(latLng); //added
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    redrawLine(); //added


                  /*  // Draw the marker, if destination location is not set
                    if (MarkerPoints.size() < 2) {

                        mLatitude = location.getLatitude();
                        mLongitude = location.getLongitude();
                        LatLng point = new LatLng(mLatitude, mLongitude);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));


                        // Checks, whether start and end locations are captured
                        if (MarkerPoints.size() >= 2) {
                            for (int z = 0; z <= MarkerPoints.size(); z++) {


                                double originLat = location.getLatitude();
                                double originLng = location.getLongitude();
                                LatLng origin = new LatLng(originLat, originLng);
                                LatLng dest = (LatLng) MarkerPoints.get(1);

                                PolylineOptions option = new PolylineOptions().add(origin).add((IKEJA)).width(5).color(Color.BLUE).geodesic(true);
                                mMap.addPolyline(option);

                                // Getting URL to the Google Directions API
                                String url = getUrl(origin, dest);

                                FetchUrl downloadTask = new FetchUrl();

                                // Start downloading json data from Google Directions API
                                downloadTask.execute(url);
                            }
                        }

                    }*/
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, (float)(0.5), new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude); //you already have this

                    MarkerPoints.add(latLng); //added
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    redrawLine(); //added



                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        /*if(MarkerPoints.size() <= 2) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            LatLng currentlocation = new LatLng(mLatitude, mLongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 16));
        }*/
    /*    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()

        {
            @Override
            public void onMapClick (LatLng latLng){

                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    mMap.clear();
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                PolylineOptions option = new PolylineOptions().addAll(MarkerPoints).width(5).color(Color.BLUE).geodesic(true);
                mMap.addPolyline(option);


                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() >= 2) {
                    LatLng origin = (LatLng) MarkerPoints.get(0);
                    LatLng dest = (LatLng) MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getUrl(origin, dest);

                    FetchUrl fetchUrl = new FetchUrl();

                    // Start downloading json data from Google Directions API
                    fetchUrl.execute(url);
                }

            }
        });
*/

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                LatLng point = new LatLng(mLatitude, mLongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
            }
        } else {
            //buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }
    private void redrawLine(){

        mMap.clear();  //clears all Markers and Polylines


        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int i = 0; i < MarkerPoints.size(); i++) {
            LatLng point = MarkerPoints.get(i);
            options.add(point);
            if(lastKnownLocation != null){
             lng = lastKnownLocation.getLatitude();
             lat = lastKnownLocation.getLongitude();
            }
            LatLng lastpoint = new LatLng(lng,lat);
            latlngArray.add(lastpoint);
            Log.d("Latlng",String.valueOf(latlngArray));

        }
     //   addMarker(); //add Marker in current position
        line = mMap.addPolyline(options); //add Polyline
    }



    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

  /*  @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
         /*   if (result != null) {
                drawPath(result);
            }*/
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions().addAll(points).width(10).color(Color.RED).geodesic(true);
            MarkerOptions markerOptions = new MarkerOptions();

            //PolylineOptions options = new PolylineOptions().add(point).add(OYO).width(5).color(Color.BLUE).geodesic(true);
            //mMap.addPolyline(options);

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
            /*    lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);*/

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }

  /*  protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }*/

/*
    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }
*/

/*    @Override
    public void onConnectionSuspended(int i) {

    }*/

 /*   public void drawMarker(LatLng point) {
        MarkerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(point);

        *//**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         *//*
        if (MarkerPoints.size() == 1) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (MarkerPoints.size() == 2) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(options);

        PolylineOptions option = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < MarkerPoints.size(); z++) {
            point = MarkerPoints.get(z);
            option.add(point);
            line.setPoints(MarkerPoints);
        }
        line = mMap.addPolyline(new PolylineOptions().add(new LatLng(mLatitude,mLongitude)));
        }
*/

/*
        @Override
        public void onLocationChanged (Location location){

            // Draw the marker, if destination location is not set
            if (MarkerPoints.size() < 2) {

                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
                LatLng point = new LatLng(mLatitude, mLongitude);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                drawMarker(point);


                PolylineOptions options = new PolylineOptions().add(4235242,2).width(5).color(Color.BLUE).geodesic(true);
                for (int z = 0; z < MarkerPoints.size(); z++) {
                    LatLng src = MarkerPoints.get(z);
                    LatLng dest = MarkerPoints.get((int) (z + 0.1));
                    point = MarkerPoints.get(z);
                    options.add(point);
                    line = mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(src.latitude, src.longitude),
                                    new LatLng(dest.latitude, dest.longitude))
                            .width(5).color(Color.BLUE).geodesic(true));
            }

                }


     *//*   mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*//*

            }*/


            public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
            public boolean checkLocationPermission () {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Asking user if explanation is needed
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)) {

                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);


                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onRequestPermissionsResult ( int requestCode,
            String permissions[], int[] grantResults){
                switch (requestCode) {
                    case MY_PERMISSIONS_REQUEST_LOCATION: {
                        // If request is cancelled, the result arrays are empty.
                        if (grantResults.length > 0
                                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                            // permission was granted. Do the
                            // contacts-related task you need to do.
                            if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                                    == PackageManager.PERMISSION_GRANTED) {

                                if (mGoogleApiClient == null) {
                                    //buildGoogleApiClient();
                                }
                                mMap.setMyLocationEnabled(true);
                            }

                        } else {

                            // Permission denied, Disable the functionality that depends on this permission.
                            Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }

                    // other 'case' lines to check for other permissions this app might request.
                    // You can add here other case statements according to your requirement.
                }
            }
   /* public void drawPath(String result) {
        if (line != null) {
            mMap.clear();
        }
        mMap.addMarker(new MarkerOptions().position(new LatLng(mInitialLocation.getLatitude(),mInitialLocation.getLongitude())).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.ic_action_location)));
        try {
            // Transform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");

            DataParser dataParser = new DataParser();
            List<LatLng> list = dataParser.decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                line.setPoints(MarkerPoints);
                line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(5).color(Color.BLUE).geodesic(true));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}