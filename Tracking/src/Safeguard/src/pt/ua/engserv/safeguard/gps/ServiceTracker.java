package pt.ua.engserv.safeguard.gps;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import pt.ua.engserv.safeguard.ActivityMain;
import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.utils.Globals;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class ServiceTracker extends Service implements LocationListener, GpsStatus.Listener {

    protected String provider;
    protected LocationManager locationManager;
    protected int i = 0;
    private static boolean isRunning = false;
    private Location mLastLocation;
    private boolean isGPSFix, secondRequest = false, requestNetworkUpdate = false;
    private long mLastLocationMillis,
    				secondRequestGps;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    	isRunning = true;
    	
        Log.d("gps", "onStartCommand()");

        provider = intent.getStringExtra("locationProvider");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(provider, 400, 5, this);
        locationManager.addGpsStatusListener(this);
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        if( location != null ) {
//            onLocationChanged(location);
//        }

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate() {

        Log.d("gps", "onCreate()");

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
        
        if(location == null) {
        	
        	return;
        }
        
        Log.d("gps", "accuracy-> " + location.getAccuracy());
        
        mLastLocation = location;
        mLastLocationMillis = SystemClock.elapsedRealtime();

        /* Obter a localidade */
        try {

            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null) {

                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {

                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                morada = strReturnedAddress.toString();
            }
            else {

                morada = "Impossível determinar localização!";
            }
        } catch (IOException e) {

            morada = "Impossível determinar localização!";
        }
        
        /* Alterar a morada no launcher */
        ActivityMain.setLocation(morada);
        
        /* Preparar a notificação */
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
//
//        NotificationManager notificationManager = (NotificationManager)
//                getSystemService(NOTIFICATION_SERVICE);
//
//        notificationManager.cancel(0);
//
//        Notification noti = new NotificationCompat.Builder(this)
//                                                  .setContentTitle(morada)
//                                                  .setContentText("lat: " + latitude + " ;long: " + longitude)
//                                                  .setTicker("Actualização de localização!")
//                                                  .setDefaults(Notification.DEFAULT_SOUND)
//                                                  .setContentIntent(pIntent)
//                                                  .setWhen(System.currentTimeMillis())
//                                                  .setSmallIcon(R.drawable.ic_launcher)
//                                                  .build();
//
//        /* Lançar a notificação */
//        notificationManager.notify(0, noti);

        sendLocationToServer(location.getLatitude(), location.getLongitude());
        
        // Se o provider é a rede
        if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
        	
        	// Se a precisão é superor a 100 metros troca para GPS
	        if(location.getAccuracy() > 100) {
	        
	        	Log.d("gps", "vai trocar provide para gps");
	        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 5, this);
	        }
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    	Log.d("gps", "status changed of " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {

    	if (provider.equals(LocationManager.GPS_PROVIDER)) {
            
    		Log.d("gps", "localizaçção por gps");
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 5, this);
        }
    	else if(provider.equals(LocationManager.NETWORK_PROVIDER)) {
    		
    		Log.d("gps", "localizaçção por gps");
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 5, this);
    	}
    }

    @Override
    public void onProviderDisabled(String provider) {

    	Log.d("gps", "provider disabled: " + provider);
    	
    	if (provider.equals(LocationManager.GPS_PROVIDER)) {
            
			Log.d("gps", "localizaçção por rede");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 5, this);
        }
    	else if(provider.equals(LocationManager.NETWORK_PROVIDER)) {
    		
    		Log.d("gps", "localizaçção por gps");
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 5, this);
    	}
    }

    private void sendLocationToServer(final double latitude, final double longitude) {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {

            	SharedPreferences sharedPref =  getSharedPreferences(
                        getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
            	
            	String token = sharedPref.getString("token", "");
            	int profileId = sharedPref.getInt("profileId", -1);
            	
            	if(profileId == -1) {
            		
            		System.exit(-1);
            	}
            	
            	String backofficeAddress = getResources().getString(R.string.backoffice_address);
            	
                String URL = backofficeAddress + "webresources/Location/setCurrentLocation/" + token + "/" + Globals.androidId + "/" + profileId  + "/" + latitude + "/" + longitude;

                Log.d("location", URL);
                
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(URL);
                String text = null;
                try {
                	
                    HttpResponse response = httpClient.execute(httpGet, localContext);


                    HttpEntity entity = response.getEntity();


                    text = getASCIIContentFromEntity(entity);

                } 
                catch (Exception e) {
                    return e.getLocalizedMessage();
                }
                
                return text;
            }

            protected void onPostExecute(String results) {
                if (results!=null) {

//                    Log.d("REST_WS location", results);
//                    Toast.makeText(getApplicationContext(), "Location updated to server", Toast.LENGTH_SHORT).show();
                }
                else {

//                    Toast.makeText(getApplicationContext(), "Error updating location to server", Toast.LENGTH_SHORT).show();
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

	public static boolean isRunning() {
		
		return isRunning;
	}
    
	public void onGpsStatusChanged(int event) {
		
		Log.d("gps", "event " + event + " !");
		
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                
            	if (mLastLocation != null) {
                    isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 10000;
                }
                	
                if (isGPSFix) { // A fix has been acquired.
                    // Do something.
                	Log.d("gps", "obteu um fix");
                }
                else { // The fix has been lost.
                    // Do something.
                	Log.d("gps", "fix lost");
                	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 400, 5, this);
                }

                break;
                
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                // Do something.
                isGPSFix = true;
                Log.d("gps", "fix first");

                break;
                
            case GpsStatus.GPS_EVENT_STOPPED :
            	
            	Log.d("gps", "gps parou");
            	
            	break;
        }
    }
    
}

