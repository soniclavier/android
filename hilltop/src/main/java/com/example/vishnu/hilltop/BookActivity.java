package com.example.vishnu.hilltop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookActivity extends AppCompatActivity {
    
    RequestQueue mRequestQueue;
    private boolean useCurrentLoc = true;


    public void sendBookReqest(View view) {


        String URL = "http://hilltop-bradleyuniv.rhcloud.com/rest/bookHilltop";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", "vishnu");
        params.put("buid", "1");
        params.put("num", "2");
        params.put("to", "bradley");
        params.put("from", "ayres");



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
                EditText fromLoc = (EditText) findViewById(R.id.fromLoc);
                if (isChecked) {
                    useCurrentLoc = true;
                    fromLoc.setText("");
                    fromLoc.setEnabled(false);
                } else {
                    useCurrentLoc = false;

                    fromLoc.setEnabled(true);
                }
            }
        });
    }


}
