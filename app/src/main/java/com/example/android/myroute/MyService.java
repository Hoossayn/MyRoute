package com.example.android.myroute;



import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.CopyOnWriteArrayList;



/**
 * Created by poseidon on 7/15/17.
 */
public class MyService extends Service implements LocationListener {

    private static final String TAG = "player";

    private ServiceHandler mServiceHandler;


    private static final CopyOnWriteArrayList<Message> UNPROCESSED_EVENTS = new CopyOnWriteArrayList<>();


    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    private Messenger mMessenger;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    private Location lastKnowLocation; // lastKnowLocation


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 2; // 2 seconds

    // Declaring a Location Manager
    protected LocationManager locationManager;








    // Handler that receives messages from the thread

    @Override
    public void onCreate() {
        super.onCreate();

        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mMessenger = new Messenger(mServiceHandler);
        if ("google_sdk".equals(Build.PRODUCT)) {
            // ... disable IPv6
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }




        getLocation();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private String extractGPSWithTimeStamp(Location location) {

        String GPS_PATTERN = "latitude longitude speed altitude accuracy time(UTC) ";

        String data = String.format("%f %f %f %f %f %d", location.getLatitude(), location.getLongitude(), location.getSpeed(), location.getAltitude(), location.getAccuracy(), location.getTime());
        return data;
    }



    public Location getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                //First get lastKnowLocation  if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.i(TAG,"GPS Enabled");
                    if (locationManager != null) {
                        lastKnowLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastKnowLocation != null) {
                           // androidLoggerClass.info("GPS LOCATION starting latitude {} longitude {} ", lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
                        }
                    }
                }
                if (isNetworkEnabled) {
                    if (lastKnowLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.i(TAG,"Network Location Enabled");
                        if (locationManager != null) {
                            lastKnowLocation = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (lastKnowLocation != null) {
                              //  androidLoggerClass.info("Network LOCATION starting latitude {} longitude {} ", lastKnowLocation.getLatitude(), lastKnowLocation.getLongitude());
                            }
                        }
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(lastKnowLocation != null){
         //   ShowToastInIntentService(" Location = "+extractGPSWithTimeStamp(lastKnowLocation));
        }
        return lastKnowLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastKnowLocation = location;
        String extractGPS = extractGPSWithTimeStamp(location);
     //   ShowToastInIntentService(" Location = "+extractGPS);
        Log.i(TAG," Location = "+extractGPS);
    }

    public void ShowToastInIntentService(final String sText) {
        final Context MyContext = this;

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(MyContext, sText, Toast.LENGTH_SHORT);
                toast1.show();
            }
        });
    };

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }


    @Override
    public void onProviderDisabled(String provider) {

    }



    private class ServiceHandler extends Handler{

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg){

        }
    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
