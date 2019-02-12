package com.jarvis_abo.jarvis_service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.jarvis_abo.JA_application;
import com.jarvis_abo.MapViewActivity;
import com.jarvis_abo.R;
import com.jarvis_abo.entities.Notification;
import com.jarvis_abo.notification.NotificationNotNowActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Mxo on 22-May-16.
 */
public class JA_service extends Service {

    private final int notificationDelayTime = 60 * 60 * 1000;
    private final double coordinateSensitivity = 0.000001;
    private final IBinder iBinder = new LocalBinder();
    private String UUID;

    public static final String ACTION_OK = "OK";
    public static final String ACTION_LATER = "Later";

    private static final String TAG = "???";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (UUID == null) {

            UUID = java.util.UUID.randomUUID().toString();

        }
        currentLocationTracker();
        Toast.makeText(JA_application.getInstance(), UUID, Toast.LENGTH_LONG).show();

        return iBinder;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(JA_application.getInstance(), "onDestroy", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        currentLocationTracker();

    }

    private LocationManager locationManager;
    private LocationListener locationListener;

    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////

    private void currentLocationTracker() {
        locationManager = (LocationManager) this
                .getSystemService(JA_application.getInstance().LOCATION_SERVICE);

        locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(JA_application.getInstance(),
                        "Provider enabled: " + provider, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(JA_application.getInstance(),
                        "Provider disabled: " + provider, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onLocationChanged(Location location) {
                // Do work with new location. Implementation of this method will be covered later.

                doWorkWithNewLocation(location);
                Toast.makeText(JA_application.getInstance(), "dow Work ///////Location is changed", Toast.LENGTH_SHORT).show();
            }
        };
//        Location lct = new Location(getProviderName());
//        double ltt = 40.220995;
//        double lgt = 44.5744658;
//        lct.setLatitude(ltt);
//        lct.setLongitude(lgt);
//        doWorkWithNewLocation(lct);

        long minTime = 5 * 1000; // Minimum time interval for update in seconds, i.e. 5 seconds.
        long minDistance = 10; // Minimum distance change for update in meters, i.e. 10 meters.

// Assign LocationListener to LocationManager in order to receive location updates.
// Acquiring provider that is used for location updates will also be covered later.
// Instead of LocationListener, PendingIntent can be assigned, also instead of
// provider name, criteria can be used, but we won't use those approaches now.


        locationManager.requestLocationUpdates(getProviderName(), minTime,
                minDistance, locationListener);


    }

    private void doWorkWithNewLocation(Location location) {

        Toast.makeText(JA_application.getInstance(), "doWork///////////////////////", Toast.LENGTH_LONG).show();

        ArrayList<Notification> currentNotifications = currentNotifications(location);
        for (Notification notification : currentNotifications) {

            showNotification(notification);


        }


    }

    private String getProviderName() {
        locationManager = (LocationManager) this
                .getSystemService(JA_application.getInstance().LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(true); // Choose if you use altitude.
        criteria.setBearingRequired(true); // Choose if you use bearing.
        criteria.setCostAllowed(true); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

    public ArrayList<Notification> currentNotifications(Location location) {

        ArrayList<Notification> notifications = (ArrayList<Notification>) JA_application.getInstance().getDaoSession().getNotificationDao().loadAll();

        Date now = Calendar.getInstance().getTime();

        ArrayList<Notification> selectedNotifications = new ArrayList<>();
        for (Notification notification : notifications) {

            double distanceSquared = (notification.getLat() - location.getLatitude()) * (notification.getLat() - location.getLatitude()) + (notification.getLng() - location.getLongitude()) * (notification.getLng() - location.getLongitude());

            if (distanceSquared < coordinateSensitivity
                    && (notification.getInactiveUntil().equals(null) || notification.getInactiveUntil().before(now))
                    ) {

                selectedNotifications.add(notification);

            }
        }

        return selectedNotifications;
    }

    public void showNotification(Notification notification) {

        // prepare intent which is triggered if the
// notification is selected

        Intent intentMapView = new Intent(this, MapViewActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntentMap = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentMapView, 0);


        Intent intentNotificationNotNow = new Intent(this, NotificationNotNowActivity.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent

        Bundle b = new Bundle();
        b.putLong("notification_id", notification.getPk_id()); //Your id
        intentNotificationNotNow.putExtras(b); //Put your id to your next Intent

        PendingIntent pIntentNotificationNotNow = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intentNotificationNotNow, 0);


// build notification
// the addAction re-use the same intent to keep the example short


        android.app.Notification n = new android.app.Notification.Builder(this)
                .setContentTitle(notification.getMessage())
                .setContentText("")
                .setSmallIcon(R.mipmap.logo)
                .setContentIntent(pIntentMap)
                .setAutoCancel(true)
                .addAction(R.mipmap.jarvis_logo, "OK", pIntentMap)
                .addAction(R.mipmap.ic_launcher, "Next time", pIntentNotificationNotNow).build();


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(new BigDecimal(notification.getPk_id()).intValueExact(), n);

    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public class LocalBinder extends Binder {

        public JA_service getJA_service() {

            return JA_service.this;

        }

    }


}
