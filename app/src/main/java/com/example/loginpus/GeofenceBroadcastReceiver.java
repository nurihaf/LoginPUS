package com.example.loginpus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence triggered", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        
        if (geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving geofence event");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList){
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        Uri notification3 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Ringtone r3 = RingtoneManager.getRingtone(context.getApplicationContext(), notification3);



        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                r3.stop();
                Toast.makeText(context, "You are inside radius", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("YOU ARE INSIDE RADIUS", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                r3.stop();
                Toast.makeText(context, "You move within radius", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("YOU MOVE INSIDE RADIUS", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                r3.play();
                Toast.makeText(context, "<!YOU EXITED THE RADIUS!>", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("WARNING! YOU ARE OUTSIDE RADIUS", "", MapsActivity.class);
                break;
        }

    }
}