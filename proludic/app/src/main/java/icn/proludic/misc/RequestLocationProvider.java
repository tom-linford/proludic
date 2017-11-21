package icn.proludic.misc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import icn.proludic.R;

import static icn.proludic.misc.Constants.LOCATION_SERVICES;

/**
 * Author:  Bradley Wilson
 * Date: 20/04/2017
 * Package: icn.proludic.misc
 * Project Name: proludic
 */

public class RequestLocationProvider {


    public static interface LocationCallback {
        public void onNewLocationAvailable(GPSCoordinates location);
    }

    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    public static void requestSingleUpdate(final Context context, final LocationCallback callback, Activity activity, LocationManager locationManager, boolean isNetworkEnabled, boolean isGPSEnabled) {
        Utils utils = new Utils(context);
        checkPermission(context);
        if (isNetworkEnabled) {
            Log.e("tom", "network is enabled");
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    callback.onNewLocationAvailable(new GPSCoordinates(location));
                }

                @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                @Override public void onProviderEnabled(String provider) { }
                @Override public void onProviderDisabled(String provider) { }
            }, null);
        } else {
            if (isGPSEnabled) {
                Log.e("tom", "gps is enabled");
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                locationManager.requestSingleUpdate(criteria, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        callback.onNewLocationAvailable(new GPSCoordinates(location));
                    }

                    @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
                    @Override public void onProviderEnabled(String provider) { }
                    @Override public void onProviderDisabled(String provider) { }
                }, null);
            }
        }

        if (!isNetworkEnabled && !isGPSEnabled) {
            //get back to this
            Log.e("tom", "neither are enabled");
            utils.showStandardDialog(context, context.getString(R.string.location_services), context.getString(R.string.turn_on_location_settings), context.getString(R.string.settings), context.getString(R.string.cancel), true, true, LOCATION_SERVICES, activity);
        }
    }

    private static void checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                Log.e("Location Permission", "Location Permission is Granted");
        } else {

        }
    }

    public static class GPSCoordinates {
        public Location location;
        GPSCoordinates(Location location) {
            this.location = location;
        }
    }


}
