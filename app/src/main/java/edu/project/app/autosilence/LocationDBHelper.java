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
    private static final String TAG = "LocationDBHelper";

    //Database constants
    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 1;

    //Table constants
    private static final String COLUMN_NAME_ID = "id";
    private static final String TABLE_NAME = "locations";
    private static final String COLUMN_NAME_LATITUDE = "lat";
    private static final String COLUMN_NAME_LONGITUDE = "lng";
    private static final String COLUMN_NAME_RADIUS = "radius";
    private static final String COLUMN_NAME_NAME = "name";
    private static final String COLUMN_NAME_ADDRESS = "address";


    public LocationDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createQuery = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME_NAME + " TEXT,"
                + COLUMN_NAME_LATITUDE + " REAL,"
                + COLUMN_NAME_LONGITUDE + " REAL,"
                + COLUMN_NAME_RADIUS + " REAL,"
                + COLUMN_NAME_ADDRESS + " TEXT );";
        Log.d(TAG, "onCreate: Executing "+ createQuery);
        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insert(final String name, final double latitude, final double longitude,final float radius, final String address) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME_NAME, name);
        contentValues.put(COLUMN_NAME_LATITUDE, latitude);
        contentValues.put(COLUMN_NAME_LONGITUDE, longitude);
        contentValues.put(COLUMN_NAME_RADIUS, radius);
        contentValues.put(COLUMN_NAME_ADDRESS, address);
        boolean result =  database.insert(TABLE_NAME,null,contentValues)>0;
        database.close();
        return result;
    }

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
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()){
            AutoSilenceLocation location = new AutoSilenceLocation(
                    cursor.getInt(columnIndexId),
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
