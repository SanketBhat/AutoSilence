package edu.project.app.autosilence;

/**
 * Created by Sanket on 19-02-2018.
 * Class represents the added geofence location for auto silence
 */

public class AutoSilenceLocation {
    //Unique id for the geofence location.
    private int id;
    //Name and address for the geofencing area
    private String name, address;
    //Latitude and Longitude for the geofencing
    private float lat;
    private float lng;
    //Determines the radius of the geofencing area.
    private float radius;
    //RequestID for the Geofences.
    private String requestId;

    AutoSilenceLocation(int id, String requestId, String name, float lat, float lng, float radius, String address) {
        this.name = name;
        this.requestId = requestId;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public float getRadius() {
        return radius;
    }

    public int getId() {
        return id;
    }

    public String getRequestId() {
        return requestId;
    }
}
