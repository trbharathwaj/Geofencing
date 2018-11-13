package com.example.ankitjena.geofenceapp;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (geofencingEvent.hasError()) {
                String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
                Log.e(TAG, errorMessage);
                return;
            }
            int geofenceTransition = geofencingEvent.getGeofenceTransition();
            if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                Log.i(TAG, "User entered geofence.");
                try {
                    callWebService("IN");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                Log.i(TAG, "User left geofence.");
                try {
                    callWebService("OUT");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                // Log the error.
                Log.i(TAG, "Geofence transition event error");
            }
        }
    }

    private void callWebService(String relGeofenceLocation) throws JSONException {
        RequestQueue queue = Volley.newRequestQueue(this);
        String endpoint = "http://msecbrad.byethost24.com/write.php";
        JSONObject postparams = new JSONObject();
        Date currentTime = Calendar.getInstance().getTime();
        postparams.put("Timestamp", currentTime.toString());
        postparams.put("Location", relGeofenceLocation);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                endpoint, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Webservice response received : \n" + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Webservice error : \n" + error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

}
