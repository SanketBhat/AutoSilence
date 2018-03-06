package edu.project.app.autosilence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                //If anything not proper just toast it and return.
                String errorMessage = getErrorString(geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                return;
            }
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            // Get the transition type.
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Toast.makeText(context, "Entered the geofencing area, Activating silent mode", Toast.LENGTH_SHORT).show();
                if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Toast.makeText(context, "Exited the geofencing area, Activating ringing mode", Toast.LENGTH_SHORT).show();
                if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                Toast.makeText(context, "An unknown event triggered", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
            //AudioManager is expected to give NullPointerException sometime.
            Toast.makeText(context, "NullPointer Exception from AudioManager!!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            //An Unknown Exception. Toast it.
            Toast.makeText(context, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
        }
    }

    //The function return appropriate error messages based on the geofence error codes
    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "The Geofencing Api is not available now.";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "This application has too many geofences.";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pendingIntents are provided in addGeofences";
            default:
                return "An Unknown Error Occurred!";
        }
    }
}
