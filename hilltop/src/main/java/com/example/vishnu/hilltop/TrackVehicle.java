package com.example.vishnu.hilltop;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackVehicle extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private LatLng previous;
    private LocationManager locationManager;
    private String provider;
    private static final int REQUEST_FINE_LOC = 0;
    private TextView eta;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_vehicle);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        IntentFilter filter = new IntentFilter("LOCATION_UPDATE");
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, filter);
        Spinner dropdown = (Spinner) findViewById(R.id.chooseVehicle);
        String[] items = new String[]{"Vehicle One", "Vehicle Two"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, items);
        dropdown.setAdapter(adapter);
    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double lat = intent.getDoubleExtra("lat", 0.0);
            double lon = intent.getDoubleExtra("lon", 0.0);
            float bearing = intent.getFloatExtra("bearing", 0.0f);
            LatLng location = new LatLng(lat, lon);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
            mMap.clear();

            //GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            //        .image(BitmapDescriptorFactory.fromResource(R.drawable.directionarrow))
            //        .position(location,45f,45f);
            //mMap.addGroundOverlay(newarkMap);

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.directionarrow))
                    .anchor(0.5f, 0.5f)
                    .position(location)
                    .flat(true)
                    .rotation(bearing));


            CameraPosition cameraPosition = CameraPosition.builder()
                    .target(location)
                    .bearing(45)
                    .zoom(16f)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    4000, null);
            Log.d("receiver", "Got message: " + lat + "," + lon);
            previous = location;
            eta =(TextView)findViewById(R.id.etaLabel);
            Location vehLoc = new Location("hilltop");
            vehLoc.setLongitude(lon);
            vehLoc.setLatitude(lat);
            String dist = getDistance(vehLoc, myLocation);
            eta.setText("Distance = " + dist);
        }

        private float getBearing(LatLng from, LatLng to) {
            Location fromL = getLocation(from);
            Location toL = getLocation(to);
            return fromL.bearingTo(toL);
        }

        private Location getLocation(LatLng location) {
            Location loc = new Location("loc");
            loc.setLatitude(location.latitude);
            loc.setLongitude(location.longitude);
            return loc;
        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_FINE_LOC);
        }
        moveToCurrentLocation();
    }



    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TrackVehicle Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.vishnu.hilltop/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "TrackVehicle Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.vishnu.hilltop/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveToCurrentLocation();

                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocation = locationManager.getLastKnownLocation(provider);
        if (myLocation == null) {
            myLocation = new Location("bradley");
            myLocation.setLatitude(40.6981432);
            myLocation.setLongitude(-89.6182068);
        }
        previous = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
        System.out.println("LAT LONG "+myLocation.getLatitude()+","+myLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(previous));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(previous));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(previous)
                .bearing(45)
                .zoom(16f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                4000, null);
    }

    private String getDistance(Location loc1, Location loc2) {
        double lon1 = loc1.getLongitude();
        double lat1 = loc1.getLatitude();
        double lon2 = loc2.getLongitude();
        double lat2 = loc2.getLatitude();
        String distance = googleDistanceApi(lat1,lon1,lat2,lon2).getDistance();
        return distance;
    }

    public Distance googleDistanceApi(final double lat1, final double lon1, final double lat2, final double lon2){
        final Distance parsedDistance = new Distance();
        //no point for this thread, this has to be modified as a different thread that updates the distance from bg.s
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lon1 + "&destination=" + lat2 + "," + lon2 + "&sensor=false&units=imperial&mode=driving");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance.setDistance(distance.getString("text"));

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    class Distance {
        private String distance;
        public void setDistance(String distance) {
            this.distance = distance;
        }
        public String getDistance() {
            return distance;
        }
    }
}
