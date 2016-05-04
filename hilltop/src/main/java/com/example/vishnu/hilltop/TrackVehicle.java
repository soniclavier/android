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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrackVehicle extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private LatLng previous;
    private float oldBearing;
    private LocationManager locationManager;
    private String provider;
    private static final int REQUEST_FINE_LOC = 0;
    private TextView eta;
    private Marker vehicleOne;
    private Marker userLoc;
    private boolean trackVehicle = false;
    private long lastNotification = 0;
    private static String buid;
    public String TRIP_FINISHED = "com.example.vishnu.hilltop.TrackVehicle$TripCompleteActionReceiver";
    private final String BUID = "buid";
    Location myLocation;
    NotificationManager notificationManager;
    static RequestQueue mRequestQueue;
    Boolean pending = false;


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
        Switch trackVehicleSwitch = (Switch) findViewById(R.id.track);
        trackVehicleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    trackVehicle = true;
                } else {
                    trackVehicle = false;
                }
            }
        });

        //need for user clicking notification
        if (vehicleOne != null) {

        }
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        buid = getIntent().getStringExtra(BUID);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();

    }

    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            double lat = intent.getDoubleExtra("lat", 0.0);
            double lon = intent.getDoubleExtra("lon", 0.0);
            float bearing = intent.getFloatExtra("bearing", 0.0f);


            LatLng location = new LatLng(lat, lon);

            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16));


            //GroundOverlayOptions newarkMap = new GroundOverlayOptions()
            //        .image(BitmapDescriptorFactory.fromResource(R.drawable.directionarrow))
            //        .position(location,45f,45f);
            //mMap.addGroundOverlay(newarkMap);
            Log.d("receiver", "Got message: " + lat + "," + lon);
            float[] distInfo = new float[3];
            if (previous != null) {
                Location.distanceBetween(previous.latitude, previous.longitude, location.latitude, location.longitude, distInfo);
                if (distInfo[0] < 10) {
                    //keep old bearing
                    bearing = oldBearing;

                }
            }
            oldBearing = bearing;

            eta = (TextView) findViewById(R.id.etaLabel);
            Location vehLoc = new Location("hilltop");
            vehLoc.setLongitude(lon);
            vehLoc.setLatitude(lat);
            Location markerLocation = new Location("markerLocation");
            markerLocation.setLatitude(userLoc.getPosition().latitude);
            markerLocation.setLongitude(userLoc.getPosition().longitude);
            String dist = getDistance(vehLoc, markerLocation);
            eta.setText("Distance = " + dist);
            checkPendingRide(buid);

            if (dist != null && dist.contains("ft") && pending) {
                double feet = Double.parseDouble(dist.substring(0, dist.indexOf(" ")));
                System.out.println(feet);
                if (feet < 300 && (lastNotification == 0 || (System.currentTimeMillis() - lastNotification > 0))) {
                    lastNotification = System.currentTimeMillis();
                    // use System.currentTimeMillis() to have a unique ID for the pending intent
                    Intent trackIntent = new Intent(context, TrackVehicle.class);
                    trackIntent.putExtras(intent.getExtras());
                    trackIntent.putExtra(BUID, buid);
                    PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), trackIntent, 0);
                    Intent trackIntentComplete = new Intent(context, TrackVehicle.class);
                    trackIntentComplete.setAction("complete");
                    Intent tripIntent = new Intent(TRIP_FINISHED);
                    tripIntent.setAction("TRIP_COMPLETE_ACTION");
                    PendingIntent completeTripPendingIntent = PendingIntent.getBroadcast(context, 0, tripIntent, 0);
                    Notification n = new Notification.Builder(context)
                            .setContentTitle("Hilltop alert")
                            .setContentText("Your vehicle is approaching")
                            .setSmallIcon(R.drawable.ic_stat_ic_notification)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .addAction(R.drawable.ic_stat_ic_notification, "MARK TRIP AS COMPLETE", completeTripPendingIntent)
                            .build();
                    notificationManager.notify(0, n);
                }
            } else {
                System.out.println("Not displaying alert");
                System.out.println("dist = " + dist);
                System.out.println("pending = " + pending);
            }

            System.out.println(distInfo[0]);
            if (vehicleOne == null || distInfo[0] > 0) {
                if (vehicleOne != null)
                    vehicleOne.remove();
                System.out.println("updating marker");
                vehicleOne = mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.directionarrow))
                        .anchor(0.5f, 0.5f)
                        .position(location)
                        .flat(true)
                        .rotation(bearing));
                previous = location;
            }

            if (trackVehicle) {
                CameraPosition cameraPosition = CameraPosition.builder()
                        .target(location)
                        .bearing(45)
                        .zoom(16f)
                        .build();
                Log.d("receiver", "tracking is on");
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                        4000, null);
            }


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
        System.out.println("provider is " + provider);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOC);
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

    @Override
    public void onLocationChanged(Location location) {
        if (userLoc != null)
            userLoc.remove();
        LatLng myLocLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        System.out.println("LAT LONG " + location.getLatitude() + "," + location.getLongitude());
        userLoc = mMap.addMarker(new MarkerOptions().draggable(true).position(myLocLatLng));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocLatLng));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(myLocLatLng)
                .bearing(45)
                .zoom(16f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                4000, null);
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

    public static class TripCompleteActionReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            String act = "TRIP_COMPLETE_ACTION";
            System.out.println("on receive");
            if (intent.getAction().equals(act)) {
                System.out.println("mark my earliest approved request complete");
                String URL = "http://hilltop-bradleyuniv.rhcloud.com/rest/markTripComplete/" + buid;

                JsonObjectRequest req = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.i("MarkTripComplete", response.getString("status"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
                mRequestQueue.add(req);
                ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).cancel(0);
            }


        }
    }

    private Location getCurrentStudentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        myLocation = locationManager.getLastKnownLocation(provider);
        return myLocation;

    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (provider == null) {
            //try to get GPS location provider
            provider = LocationManager.GPS_PROVIDER;
        }
        if (provider != null)
            myLocation = locationManager.getLastKnownLocation(provider);
        if (myLocation == null) {
            Toast toast = Toast.makeText(this, "Could not get your location, defaulting to Bradley Univeristy location", Toast.LENGTH_LONG);
            toast.show();
            myLocation = new Location("bradley");
            myLocation.setLatitude(40.6981432);
            myLocation.setLongitude(-89.6182068);
        }
        if (userLoc != null)
            userLoc.remove();
        LatLng myLocLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        System.out.println("LAT LONG " + myLocation.getLatitude() + "," + myLocation.getLongitude());
        userLoc = mMap.addMarker(new MarkerOptions().draggable(true).position(myLocLatLng));
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocLatLng));
        CameraPosition cameraPosition = CameraPosition.builder()
                .target(myLocLatLng)
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
        String distance = googleDistanceApi(lat1, lon1, lat2, lon2).getDistance();
        return distance;
    }

    public Distance googleDistanceApi(final double lat1, final double lon1, final double lat2, final double lon2) {
        final Distance parsedDistance = new Distance();
        //no point for this thread, this has to be modified as a different thread that updates the distance from bg.s
        Thread thread = new Thread(new Runnable() {
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

    public void checkPendingRide(String id) {
        String URL = "http://hilltop-bradleyuniv.rhcloud.com/rest/checkPending/" + id;

        JsonObjectRequest req = new JsonObjectRequest(URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    pending = Boolean.parseBoolean(response.getString("pending"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mRequestQueue.add(req);
    }


    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public void loadBooking(View view) {

        Intent intent = new Intent(TrackVehicle.this, BookActivity.class);
        String distance = "";

        Location bradley = new Location("bradley");
        bradley.setLatitude(40.6981432);
        bradley.setLongitude(-89.6182068);

        Location studentLoc = new Location("studentLoc");
        studentLoc.setLatitude(userLoc.getPosition().latitude);
        studentLoc.setLongitude(userLoc.getPosition().longitude);
        distance = getDistance(bradley,studentLoc);

        Bundle b = new Bundle();
        b.putString(BUID, getIntent().getStringExtra("buid"));
        b.putDouble("lat", userLoc.getPosition().latitude);
        b.putDouble("lon", userLoc.getPosition().longitude);
        b.putString("distance",distance);
        intent.putExtras(b);
        startActivity(intent);
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
