package icn.proludic.models;

/**
 * Author:  Bradley Wilson
 * Date: 22/05/2017
 * Package: icn.proludic.models
 * Project Name: proludic
 */

public class NearestParkModel {

    private String name, distance;
    private double longitude, latitude;

    public NearestParkModel(String name, String distance, double latitude, double longitude) {
        this.name = name;
        this.distance = distance;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }


    public String getName() {
        return name;
    }

    public String getDistance() {
        return distance;
    }
}
