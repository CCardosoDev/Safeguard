package com.es.georeferencingservice;

/**
 *
 * @author claudia cardoso - 45772
 *
 * Defines decimal degrees coordinate type.
 *
 */
public class Coordinate {

    /**
     * Latitude value of the coordinate - north-south position of the location.
     */
    private double latitude = -91.0;
    /**
     * Longitude value of the coordinate - east-west position of the location.
     */
    private double longitude = -181.0;

    public Coordinate() {
    }

    /**
     *
     * Data type constructor.
     *
     * @param latitude
     * @param longitude
     */
    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
