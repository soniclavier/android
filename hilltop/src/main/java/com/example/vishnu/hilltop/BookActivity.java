package com.example.vishnu.hilltop;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;

import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookActivity extends AppCompatActivity {

    RequestQueue mRequestQueue;

    public void sendBookReqest(View view) {

        String URL = "http://hilltop-bradleyuniv.rhcloud.com/rest/bookHilltop";

        String name = ((EditText)findViewById(R.id.studentName)).getText().toString();
        String buid = ((EditText)findViewById(R.id.bradleyID)).getText().toString();
        String num = ((EditText)findViewById(R.id.numStudents)).getText().toString();
        String to = ((EditText)findViewById(R.id.destination)).getText().toString();
        String from = ((EditText)findViewById(R.id.fromLoc)).getText().toString();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("buid", buid);
        params.put("num", num);
        params.put("to", to);
        params.put("from", from);



        JsonObjectRequest req = new JsonObjectRequest(URL,new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            System.out.println("Success "+response);
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

    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        //sb.append(address.getLocality()).append("\n");
                        //sb.append(address.getPostalCode()).append("\n");
                        //sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", result);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Unable to get address for this location";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);

        // Start the queue
        mRequestQueue.start();



        Switch takeCurrentLocation = (Switch) findViewById(R.id.switchBook);
        takeCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.bookButton).setEnabled(true);
                EditText fromLoc = (EditText) findViewById(R.id.fromLoc);
                if (isChecked) {
                    fromLoc.setText("");
                    fromLoc.setEnabled(false);
                    Bundle b = getIntent().getExtras();
                    if (b!=null) {
                        Double latitude = b.getDouble("lat");
                        Double longitude = b.getDouble("lon");
                        getAddressFromLocation(latitude,longitude,getApplicationContext(),new UpdateFromLocHandler());
                    } else {
                        fromLoc.setText("Could not retrieve your current location");
                        findViewById(R.id.bookButton).setEnabled(false);
                    }
                } else {
                    fromLoc.setText("");
                    fromLoc.setEnabled(true);
                }
            }
        });
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            Double latitude = b.getDouble("lat");
            Double longitude = b.getDouble("lon");
            getAddressFromLocation(latitude,longitude,getApplicationContext(),new UpdateFromLocHandler());
        }
    }

    private class UpdateFromLocHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            EditText fromLoc = (EditText) findViewById(R.id.fromLoc);
            fromLoc.setText(locationAddress);
        }
    }

    public void loadBookingStatus(View view) {
        System.out.println("in loadBookingStatus");
        Intent intent = new Intent(this,BookingStatus.class);
        startActivity(intent);
    }


}
