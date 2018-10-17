package edu.project.app.autosilence;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnCompleteListener, LocationListAdapter.RecyclerViewClickCallbacks {

    //Constant to determine the activity from which the result is returned.
    public static final String ACTIVITY_FROM = "activityFrom";

    //Constant key for Preference to store the request id count
    public static final String REQUEST_ID_EXTRA = "geofenceRequestId";

    //Constant tag for logging purpose.
    private static final String TAG = MainActivity.class.getSimpleName();

    Snackbar snackbar = null;

    //Geofencing client
    GeofencingClient geofencingClient;
    LocationLoadTask loadTask;

    //The recyclerview adapter for the locationList.
    private LocationListAdapter listAdapter;
    //Database instance to store the added geofences.
    private LocationDBHelper locationDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            //Setting main content
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //Setting up floating action button
            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    startActivityForResult(intent, 777);
                }
            });

            //Requesting the permissions
            if ((Build.VERSION.SDK_INT >= 23)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissions = new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.ACCESS_FINE_LOCATION};
                    ActivityCompat.requestPermissions(this, permissions, 777);
                }
            }

            //Requesting permission to change Audio Settings
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }

            //Initialising GeofencingClient
            geofencingClient = LocationServices.getGeofencingClient(this);

            //Preparing the recyclerview
            locationDBHelper = new LocationDBHelper(this);
            RecyclerView locationList = findViewById(R.id.rv_list_locations);
            locationList.setLayoutManager(new LinearLayoutManager(this));
            locationList.setAdapter((listAdapter = new LocationListAdapter(null)));
            //Register for RecyclerView Click Callback
            listAdapter.setRecyclerViewClickCallbacks(this);
            new ItemTouchHelper(listAdapter.getSwipeHelper()).attachToRecyclerView(locationList);
        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            Log.e(TAG, "onCreate: An Exception occurred!", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataIntoAdapter();
    }

    void loadDataIntoAdapter() {
        loadTask = new LocationLoadTask();
        loadTask.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            //Started activity is completed with result code RESULT_OK. If not just ignore.
            if (resultCode == RESULT_OK) {
                String fromActivity = data.getStringExtra(ACTIVITY_FROM);

                //Check if the resulted activity is Maps activity or the ConfirmDialogActivity
                if (fromActivity != null && fromActivity.equals(MapsActivity.FROM_MAPS_ACTIVITY)) {
                    //Activity resulted is the MapsActivity.
                    //Retrieve selected latitude and longitude.
                    double lat = data.getDoubleExtra(ConfirmDialogActivity.GEO_LATITUDE, 0.0);
                    double lng = data.getDoubleExtra(ConfirmDialogActivity.GEO_LONGITUDE, 0.0);
                    float rad = 100.0f;
                    Intent intent = new Intent(this, ConfirmDialogActivity.class)
                            .putExtra(ConfirmDialogActivity.GEO_LATITUDE, lat)
                            .putExtra(ConfirmDialogActivity.GEO_LONGITUDE, lng)
                            .putExtra(ConfirmDialogActivity.GEO_RADIUS, rad);
                    startActivityForResult(intent, 777);

                } else {
                    //Activity resulted is the ConfirmDialogActivity.
                    Double lat = data.getDoubleExtra(ConfirmDialogActivity.GEO_LATITUDE, 0.0);
                    Double lng = data.getDoubleExtra(ConfirmDialogActivity.GEO_LONGITUDE, 0.0);
                    Float rad = data.getFloatExtra(ConfirmDialogActivity.GEO_RADIUS, 0.0f);
                    String name = data.getStringExtra(ConfirmDialogActivity.GEO_NAME);
                    String address = data.getStringExtra(ConfirmDialogActivity.GEO_ADDRESS);
                    int idCount = getPreferences(MODE_PRIVATE).getInt(REQUEST_ID_EXTRA, 0);
                    SharedPreferences.Editor prefEdit = getPreferences(MODE_PRIVATE).edit();
                    prefEdit.putInt(REQUEST_ID_EXTRA, idCount + 1).apply();
                    String requestId = REQUEST_ID_EXTRA + idCount;
                    //Adding selected location to the geofencing client
                    ArrayList<Geofence> geofenceList = new ArrayList<>();
                    geofenceList.add(new Geofence.Builder()
                            .setCircularRegion(lat, lng, rad)
                            .setRequestId(requestId)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .setExpirationDuration(Geofence.NEVER_EXPIRE)
                            .build());
                    GeofencingRequest request = new GeofencingRequest.Builder()
                            .addGeofences(geofenceList)
                            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build();
                    try {
                        geofencingClient.addGeofences(request, getPendingIntent()).addOnCompleteListener(MainActivity.this);
                        locationDBHelper.insert(requestId, name, lat, lng, rad, address);
                    } catch (SecurityException e) {
                        Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
        }
    }

    private PendingIntent getPendingIntent() {
        try {
            Intent intent = new Intent(this, GeofenceReceiver.class);
            return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } catch (Exception e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (snackbar != null) snackbar.dismiss();
        snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Adding geofence done", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onItemClick(View v, int position) {
        //Triggers when recyclerview item clicked.
        if (snackbar != null) snackbar.dismiss();
        snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Item clicked!", Snackbar.LENGTH_SHORT);
        snackbar.show();

    }

    @Override
    public boolean onItemLongClick(View v, int position) {
        //Triggers when recyclerview item long clicked.
        if (snackbar != null) snackbar.dismiss();
        snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Long clicked on the item", Snackbar.LENGTH_SHORT);
        snackbar.show();

        return false;
    }

    @Override
    public void onItemSwipe(final int position) {
        if (geofencingClient == null) {
            geofencingClient = LocationServices.getGeofencingClient(this);
        }
        AutoSilenceLocation location = listAdapter.getItem(position);
        ArrayList<String> requestIdList = new ArrayList<>();
        requestIdList.add(location.getRequestId());
        try {
            geofencingClient.removeGeofences(requestIdList).addOnCompleteListener(MainActivity.this);
            if (locationDBHelper.remove(location.getId())) {
                if (snackbar != null) snackbar.dismiss();
                snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Item deleted!", Snackbar.LENGTH_SHORT);
                snackbar.show();
                listAdapter.removeLocation(position);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, Log.getStackTraceString(e), Toast.LENGTH_LONG).show();
            if (snackbar != null) snackbar.dismiss();
            snackbar = Snackbar.make(findViewById(R.id.mainActivityLayout), "Can't delete!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            listAdapter.notifyDataSetChanged();
        }
    }

    @SuppressWarnings("StaticFieldLeak")
    class LocationLoadTask extends AsyncTask<Void, Void, ArrayList<AutoSilenceLocation>> {
        @Override
        protected ArrayList<AutoSilenceLocation> doInBackground(Void... voids) {
            return locationDBHelper.getAllData();
        }

        @Override
        protected void onPostExecute(ArrayList<AutoSilenceLocation> autoSilenceLocations) {
            super.onPostExecute(autoSilenceLocations);
            listAdapter.setLocations(autoSilenceLocations);
        }
    }

}
