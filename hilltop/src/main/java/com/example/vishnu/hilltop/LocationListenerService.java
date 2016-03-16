package com.example.vishnu.hilltop;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmListenerService;


/**
 * Created by vishnu on 1/27/16.
 */
public class LocationListenerService extends GcmListenerService{

    private static final String TAG = "LocationListenerService";
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            String parts[] = message.split(",");
            double lat = Double.parseDouble(parts[0]);
            double lon = Double.parseDouble(parts[1]);
            float bearing = Float.parseFloat(parts[2]);
            Location targetLocation = new Location("");
            targetLocation.setLatitude(lat);
            targetLocation.setLongitude(lon);
            Log.d(TAG, lat + "," + lon);

            Intent intent = new Intent("LOCATION_UPDATE");
            intent.putExtra("lat",lat);
            intent.putExtra("lon",lon);
            intent.putExtra("bearing",bearing);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);



        } else {
            // normal downstream message.
        }


    }




}
