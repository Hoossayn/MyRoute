package com.example.android.myroute.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;

import com.example.android.myroute.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;

public class map_history extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
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


        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);

            for (int a = 0; a < ary.length; a++) {

                String[] finalsplit = Arrays.toString(ary).split(",");
                String[] anotherfinalsplit = Arrays.toString(new String[]{finalsplit[a]}).split("&");

                Log.d("Individual Items", Arrays.toString(new String[]{finalsplit[a]}));

                try {

                    String latString = anotherfinalsplit[0].replace("[", "");
                    String longString = anotherfinalsplit[1].replace("]", "");


                    latit = Double.parseDouble(latString);
                    longit = Double.parseDouble(longString);

                    options.add(new LatLng(latit, longit));
                    line = mMap.addPolyline(options); //add Polyline
                    LatLng startpoint = new LatLng(latit, longit);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(startpoint));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            catch (Exception e){e.printStackTrace();}

            }
    }


    /*public class PolylineTileProvider implements TileProvider {
        private static final String TAG = "TileOverlay";
        private final int mTileSize = 256;
        private final SphericalMercatorProjection mProjection = new SphericalMercatorProjection(mTileSize);
        private final int mScale = 2;
        private final int mDimension = mScale * mTileSize;
        private final List<PolylineOptions> polylines;

        public PolylineTileProvider(List<PolylineOptions> polylines) {
            this.polylines = polylines;
        }

        @Override
        public Tile getTile(int x, int y, int zoom) {
            Matrix matrix = new Matrix();
            float scale = ((float) Math.pow(2, zoom) * mScale);
            matrix.postScale(scale, scale);
            matrix.postTranslate(-x * mDimension, -y * mDimension);
            Bitmap bitmap = Bitmap.createBitmap(mDimension, mDimension, Bitmap.Config.ARGB_8888); //save memory on old phones
            Canvas c = new Canvas(bitmap);
            c.setMatrix(matrix);
            drawCanvasFromArray(c, scale);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return new Tile(mDimension, mDimension, baos.toByteArray());
        }

        private void drawCanvasFromArray(Canvas c, float scale) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setShadowLayer(0, 0, 0, 0);
            paint.setAntiAlias(true);

            if (polylines != null) {
                for (int i = 0; i < polylines.size(); i++) {
                    List<LatLng> route = polylines.get(i).getPoints();
                    paint.setColor(polylines.get(i).getColor());
                    paint.setStrokeWidth(getLineWidth(polylines.get(i).getWidth(), scale));
                    Path path = new Path();
                    if (route != null && route.size() > 1) {
                        Point screenPt1 = mProjection.toPoint(route.get(0)); //first point
                        MarkerOptions m = new MarkerOptions();
                        m.position(route.get(0));
                        path.moveTo((float) screenPt1.x, (float) screenPt1.y);
                        for (int j = 1; j < route.size(); j++) {
                            Point screenPt2 = mProjection.toPoint(route.get(j));
                            path.lineTo((float) screenPt2.x, (float) screenPt2.y);
                        }
                    }
                    c.drawPath(path, paint);
                }
            }
        }

        private float getLineWidth(float width, float scale) {
            return width / (scale);
        }
    }*/
}

