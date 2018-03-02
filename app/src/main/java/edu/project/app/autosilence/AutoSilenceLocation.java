package edu.project.app.autosilence;

/**
 * Created by Sanket on 19-02-2018.
 * Class represents the added geofence location for auto silence
 */

public class AutoSilenceLocation {
    private int id;
    private String name, address;
    private float lat;
    private float lng;

    private float radius;

     AutoSilenceLocation() {
    }

    AutoSilenceLocation(int id, String name, float lat, float lng, float radius, String address) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.id = id;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
