package pt.ua.engserv.safeguard.gps;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pt.ua.engserv.safeguard.R;

/**
 * Created by luis on 10/5/13.
 */
public class ServiceTracker extends Service implements LocationListener {

    protected String provider;
    protected LocationManager locationManager;
    protected int i = 0;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("ServiceTracker", "onStartCommand()");

        provider = intent.getStringExtra("locationProvider");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(provider, 400, 5, this);

        Location location = locationManager.getLastKnownLocation(provider);

        if( location != null ) {
            onLocationChanged(location);
        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {

        Log.d("ServiceTracker", "onCreate()");

        Toast.makeText(this, "GPS Tracking ON", Toast.LENGTH_SHORT).show();

        if( provider != null) {

            locationManager.requestLocationUpdates(provider, 400, 5, this);
        }
    }

    @Override
    public void onDestroy() {

        Toast.makeText(this, "GPS Tracking OFF", Toast.LENGTH_SHORT).show();

        locationManager.removeUpdates(this);
        locationManager = null;
    }


    @Override
    public void onLocationChanged(Location location) {

        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        String morada;

        Geocoder geocoder = new Geocoder(this);

        /* Obter a localidade */
        try {

            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {

                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                morada = strReturnedAddress.toString();
            }
            else {

                morada = "No Address returned!";
            }
        } catch (IOException e) {

            morada = "Canont get Address!";
        }

        /* Preparar a notificação */
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancel(0);

        Notification noti = new NotificationCompat.Builder(this)
                                                  .setContentTitle(morada)
                                                  .setContentText("lat: " + latitude + " ;long: " + longitude)
                                                  .setTicker("Actualização de localização!")
                                                  .setDefaults(Notification.DEFAULT_SOUND)
                                                  .setContentIntent(pIntent)
                                                  .setWhen(System.currentTimeMillis())
                                                  .setSmallIcon(R.drawable.ic_launcher)
                                                  .build();

        /* Lançar a notificação */
        notificationManager.notify(0, noti);

        sendLocationToServer(location.getLatitude(), location.getLongitude());
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {

        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


    private void sendLocationToServer(final double latitude, final double longitude) {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {

                String URL = "http://10.10.0.1:8080/Safeguard_android_backoffice/webresources/Location/setCurrentLocation/ID_EXTERNO/" + latitude + "/" + longitude;

                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(URL);
                String text = null;
                try {
                    HttpResponse response = httpClient.execute(httpGet, localContext);


                    HttpEntity entity = response.getEntity();


                    text = getASCIIContentFromEntity(entity);


                } catch (Exception e) {
                    return e.getLocalizedMessage();
                }


                return text;
            }

            protected void onPostExecute(String results) {
                if (results!=null) {

                    Log.d("REST_WS", results);
                    Toast.makeText(getApplicationContext(), "Location updated to server", Toast.LENGTH_SHORT).show();

                }
                else {

                    Toast.makeText(getApplicationContext(), "Error updating location to server", Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.execute(null, null, null);

    }

    private String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

        InputStream in = entity.getContent();

        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {

            byte[] b = new byte[4096];
            n =  in.read(b);


            if (n>0) {

                out.append(new String(b, 0, n));
            }
        }

        return out.toString();
    }
}
