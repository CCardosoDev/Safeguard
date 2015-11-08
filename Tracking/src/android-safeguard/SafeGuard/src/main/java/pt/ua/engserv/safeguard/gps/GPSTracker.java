package pt.ua.engserv.safeguard.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by luis on 10/5/13.
 */
public class GPSTracker implements Runnable, LocationListener {

    protected String provider;
    protected LocationManager locationManager;

    public GPSTracker(String provider, LocationManager locationManager) {

        this.provider = provider;
        this.locationManager = locationManager;

        locationManager.requestLocationUpdates(provider, 400, 5, this);

        Log.d("GPSTracker", "provider: " + provider);
    }

    @Override
    public void run() {



    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
