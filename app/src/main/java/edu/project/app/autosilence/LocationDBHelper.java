package edu.project.app.autosilence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Sanket on 20-02-2018.
 * Class that handles all database operations.
 */

public class LocationDBHelper extends SQLiteOpenHelper {
    //Constant TAG for logging purpose.
    private static final String TAG = "LocationDBHelper";

    //Database file name
    private static final String DATABASE_NAME = "location.db";
    //Database version
    private static final int DATABASE_VERSION = 1;

    //Table constants
    //Table name
    private static final String TABLE_NAME = "locations";
    //Unique id -> auto increment.
    private static final String COLUMN_NAME_ID = "id";
    //Stores latitude of the geofence
    private static final String COLUMN_NAME_LATITUDE = "lat";
    //Column stores longitude of the geofence
    private static final String COLUMN_NAME_LONGITUDE = "lng";
    //Column stores the radius of the geofencing area
    private static final String COLUMN_NAME_RADIUS = "radius";
    //User given or the auto-generated name for the geofence
    private static final String COLUMN_NAME_NAME = "name";
    //The requestID given to the geofencing area.
    private static final String COLUMN_NAME_REQUEST_ID = "requestId";
    //User given or auto created address for the easy identification of the area.
    private static final String COLUMN_NAME_ADDRESS = "address";


    public LocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Query for the table create.
        //All fields are created as TEXT to preserve the precision of the latitude, longitude and the radius.
        String createQuery = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_REQUEST_ID + " TEXT,"
                + COLUMN_NAME_NAME + " TEXT,"
                + COLUMN_NAME_LATITUDE + " TEXT,"
                + COLUMN_NAME_LONGITUDE + " TEXT,"
                + COLUMN_NAME_RADIUS + " TEXT,"
                + COLUMN_NAME_ADDRESS + " TEXT );";
        Log.d(TAG, "onCreate: Executing "+ createQuery);
        //Executing the above query
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //This function is not yet implemented.
    }

    //The insert function insert a single row of data into the database.
    //Returns the boolean whether or not row inserted.
    public boolean insert(final String requestId, final String name, final double latitude, final double longitude, final float radius, final String address) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_REQUEST_ID, requestId);
        contentValues.put(COLUMN_NAME_NAME, name);
        contentValues.put(COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(COLUMN_NAME_LONGITUDE, longitude);
        contentValues.put(COLUMN_NAME_RADIUS, radius);
        contentValues.put(COLUMN_NAME_ADDRESS, address);
        boolean result =  database.insert(TABLE_NAME,null,contentValues)>0;
        database.close();
        return result;
    }

    boolean remove(final int id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean isRemoved = sqLiteDatabase.delete(TABLE_NAME, COLUMN_NAME_ID + "=" + id, null) >= 0;
        sqLiteDatabase.close();
        return isRemoved;
    }

    //Function fetches all data from the database and prepares, returns the ArrayList of Locations.
    public ArrayList<AutoSilenceLocation> getAllData() {
        ArrayList<AutoSilenceLocation> locations = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        int columnIndexId = cursor.getColumnIndex(COLUMN_NAME_ID);
        int columnIndexName = cursor.getColumnIndex(COLUMN_NAME_NAME);
        int columnIndexLat = cursor.getColumnIndex(COLUMN_NAME_LATITUDE);
        int columnIndexLng = cursor.getColumnIndex(COLUMN_NAME_LONGITUDE);
        int columnIndexRad = cursor.getColumnIndex(COLUMN_NAME_RADIUS);
        int columnIndexAddress = cursor.getColumnIndex(COLUMN_NAME_ADDRESS);
        int columnIndexRequestId = cursor.getColumnIndex(COLUMN_NAME_REQUEST_ID);
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            AutoSilenceLocation location = new AutoSilenceLocation(
                    cursor.getInt(columnIndexId),
                    cursor.getString(columnIndexRequestId),
                    cursor.getString(columnIndexName),
                    cursor.getFloat(columnIndexLat),
                    cursor.getFloat(columnIndexLng),
                    cursor.getFloat(columnIndexRad),
                    cursor.getString(columnIndexAddress)
            );
            locations.add(location);
        }
        cursor.close();
        database.close();
        return locations;
    }
}
