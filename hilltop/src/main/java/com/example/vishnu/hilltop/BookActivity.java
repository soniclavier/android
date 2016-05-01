package com.example.vishnu.hilltop;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BookActivity extends AppCompatActivity {

    RequestQueue mRequestQueue;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0;
    private final int MY_READ_PHONE_STATE = 1;
    private String phoneNumber = "Unknown";

    public void sendBookReqest(View view) {

        String URL = "http://hilltop-bradleyuniv.rhcloud.com/rest/bookHilltop";
        View focusView = null;

        EditText nameView = ((EditText) findViewById(R.id.studentName));
        EditText numView = ((EditText) findViewById(R.id.numStudents));
        EditText toView = ((EditText) findViewById(R.id.destination));
        EditText fromView = ((EditText) findViewById(R.id.fromLoc));
        String name = nameView.getText().toString();
        String num = numView.getText().toString();
        String to = toView.getText().toString();
        String from = fromView.getText().toString();
        if (name.trim().equals("")) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            focusView.requestFocus();
        } else if (num.toString().equals("")) {
            numView.setError(getString(R.string.error_field_required));
            focusView = numView;
            focusView.requestFocus();
        } else if (to.trim().equals("")) {
            toView.setError(getString(R.string.error_field_required));
            focusView = toView;
            focusView.requestFocus();
        } else if (from.trim().equals("")) {
            fromView.setError(getString(R.string.error_field_required));
            focusView = fromView;
            focusView.requestFocus();
        } else {

            try {
                Integer.parseInt(num);
                HashMap<String, String> params = new HashMap<String, String>();
                String buid = getIntent().getStringExtra("buid");
                params.put("name", name);
                params.put("buid", buid);
                params.put("num", num);
                params.put("to", to);
                params.put("from", from);


                JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    VolleyLog.v("Response:%n %s", response.toString(4));
                                    System.out.println("Success " + response);
                                    String message = "Booking request sent";
                                    message += "\nBooking ID : " + response.getString("booking_id");
                                    message += "\nStatus : " + response.getString("approval_status");
                                    openDialog(message, true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                        openDialog("Could not send booking request\nPlease try again later", false);
                    }
                });

                mRequestQueue.add(req);
            } catch (NumberFormatException e) {
                numView.setError("Not a valid number");
                focusView = numView;
                focusView.requestFocus();
            }
        }

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
                        result = latitude+","+longitude;
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

        if (!isNetworkAvailable()) {
            Toast toast = Toast.makeText(this,"Please check your internet connectivity",Toast.LENGTH_LONG);
            toast.show();
        }

        Switch takeCurrentLocation = (Switch) findViewById(R.id.switchBook);
        takeCurrentLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                findViewById(R.id.bookButton).setEnabled(true);
                EditText fromLoc = (EditText) findViewById(R.id.fromLoc);
                if (isChecked) {
                    fromLoc.setText("");
                    fromLoc.setEnabled(false);
                    Bundle b = getIntent().getExtras();
                    if (b != null) {
                        Double latitude = b.getDouble("lat");
                        Double longitude = b.getDouble("lon");
                        if (isNetworkAvailable())
                            getAddressFromLocation(latitude, longitude, getApplicationContext(), new UpdateFromLocHandler());
                        else
                            fromLoc.setText(latitude + "," + longitude);
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
        if (b != null) {
            Double latitude = b.getDouble("lat");
            Double longitude = b.getDouble("lon");
            getAddressFromLocation(latitude, longitude, getApplicationContext(), new UpdateFromLocHandler());
        }


        if (checkPhoneStatePermission()) {
           getPhoneNumber();
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
        Bundle b = getIntent().getExtras();
        Intent intent = new Intent(this, BookingStatus.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    public void openDialog(String message, final boolean success) {
        final Dialog dialog = new Dialog(this); // Context, this, etc.
        dialog.setContentView(R.layout.bookingresult);
        dialog.setTitle("Booking response");
        TextView dialogText = (TextView) dialog.findViewById(R.id.dialog_info);
        dialogText.setText(message);
        if (success) {
            ((Button) dialog.findViewById(R.id.dialog_ok)).setBackgroundColor(getResources().getColor(R.color.gossipGreen));
        } else {
            ((Button) dialog.findViewById(R.id.dialog_ok)).setBackgroundColor(getResources().getColor(R.color.grey));
        }
        dialog.findViewById(R.id.dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (success) {
                    Bundle b = getIntent().getExtras();
                    Intent intent = new Intent(getBaseContext(), BookingStatus.class);
                    intent.putExtras(b);
                    startActivity(intent);
                } else {
                    dialog.dismiss();
                }
            }
        });

        dialog.findViewById(R.id.dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void callHilltop(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+13096772800"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
            return;
        }
        startActivity(callIntent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean checkPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_READ_PHONE_STATE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getPhoneNumber();

                } else {
                    System.out.println("exit the app here?");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        if (mPhoneNumber != null && !mPhoneNumber.trim().equals("") && !mPhoneNumber.contains("?"))
            phoneNumber =  mPhoneNumber;
    }
}
