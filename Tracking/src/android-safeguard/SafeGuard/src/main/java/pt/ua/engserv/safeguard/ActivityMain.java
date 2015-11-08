package pt.ua.engserv.safeguard;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import pt.ua.engserv.safeguard.gps.ServiceTracker;
import pt.ua.engserv.safeguard.parental.ActivityLauncherWarning;
import pt.ua.engserv.safeguard.settings.ActivitySettings;

public class ActivityMain extends Activity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected LocationManager locationManager;
    protected int i;
    protected String provider;
    protected String externalId = "ID_EXTERNO";
    protected String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);



        /*Intent myService = new Intent(this, MyService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, myService, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);*/

    }

    private void resgiterDevice() {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {

                String URL = "http://10.10.0.1:8080/Safeguard_android_backoffice/webresources/Registration/"
                                + androidId + "/" + externalId;

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
                }
            }
        };

        task.execute(null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onResume() {

        super.onResume();

        /* Verifica se o launcher por omissão é esta aplicação */
        if( !getCurrentLauncher().contains("safeguard") ) {

            Intent launcherWarning = new Intent(this, ActivityLauncherWarning.class);
            startActivity(launcherWarning);
        }

        /* Regista o aparelho no seridor XMPP */
        registerDeviceAtXmppServer();

        /* Regista o aparelho junto da plataforma Safeguard */
        //resgiterDevice();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onBackPressed() {

        Log.d("onBackPressed()", "back button pressed!");
    }

    public void startGPSTracking(View view) {

        /* verificar se o GPS está ligado */
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        /* Se o GPS não está ligado, abre as definições de localização */
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        List<String> providersList = locationManager.getAllProviders();

        if( providersList.contains(LocationManager.GPS_PROVIDER)) {

            provider = LocationManager.GPS_PROVIDER;
        }
        else {

            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);
        }

        Intent serviceTracker = new Intent(this, ServiceTracker.class);

        serviceTracker.putExtra("locationProvider", provider);

        startService(serviceTracker);
    }

    public void stopGPSTracking(View view) {

        stopService(new Intent(this, ServiceTracker.class));
    }


    public void launcher(View view) {

        clear();

        Context c = getApplicationContext();

        PackageManager pm = c.getPackageManager();
        ComponentName cN = new ComponentName(c, ActivityMain.class);
        ComponentName cn1 = new ComponentName(getPackageName(), getPackageName() + ".LauncherAlias1");
        ComponentName cn2 = new ComponentName(getPackageName(), getPackageName() + ".LauncherAlias2");

        /* Este launcher não faz parte das opções de escolha */
        pm.setComponentEnabledSetting(cn1, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(cn2, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        /* Utilizador escolhe o launcher */
        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(selector);

        int dis = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        if(pm.getComponentEnabledSetting(cn1) == dis) {

            dis = 3 - dis;
        }
        pm.setComponentEnabledSetting(cn1, dis, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(cn2, 3 - dis, PackageManager.DONT_KILL_APP);
    }

    public void onClickbuttonSettings(View view) {

        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
    }

    public void onClickbuttonGCM(View view) {

        //checkPlayServices();
    }

    private void clear() {

        PackageManager p = getApplicationContext().getPackageManager();

        try {

            p.clearPackagePreferredActivities(getPackageName());
        }
        catch (NullPointerException ex) {
        }
    }

    private String getCurrentLauncher() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;

        Log.d("launcherapp:", currentHomePackage);

        return currentHomePackage;
    }

    private void showHomeLauncherChooser() {

        PackageManager pm = getPackageManager();
        ComponentName cn1 = new ComponentName(getPackageName(), getPackageName() + ".LauncherAlias1");
        ComponentName cn2 = new ComponentName(getPackageName(), getPackageName() + ".LauncherAlias2");
        int dis = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;

        if(pm.getComponentEnabledSetting(cn1) == dis) {

            dis = 3 - dis;
        }
        pm.setComponentEnabledSetting(cn1, dis, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(cn2, 3 - dis, PackageManager.DONT_KILL_APP);

        /* Utilizador escolhe o launcher */
        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(selector);

        pm.setComponentEnabledSetting(cn2, dis, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(cn1, 3 - dis, PackageManager.DONT_KILL_APP);
    }

    /*private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "Não suporta GCM", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }

        Toast.makeText(this, "Suporta GCM", Toast.LENGTH_LONG).show();
        return true;
    }*/

    private void sendLocationToServer(double latitude, double longitude) {

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {

                String URL = "http://192.168.0.191";

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


                }
            }
        };

        task.execute(null, null, null);

    }


    private void registerDeviceAtXmppServer() {

        String backOfficeAddress,
                deviceId;

        backOfficeAddress = this.getResources().getString(R.string.backoffice_address);
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... args) {

                String backofficeAddress = args[0];
                String deviceId = args[1];
                String password = "safeguard";

                Log.d("REST_WS", backofficeAddress);

                String URL = "http://" + backofficeAddress + ":8080/Safeguard_android_backoffice/" +
                        "webresources/XMPP/createXmppServiceUser/" + deviceId + "/" + password;

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

                    Log.d("REST_WS", results);
                }
            }
        };

        task.execute(backOfficeAddress, deviceId);
    }


    private String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();


        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);


            if (n>0) out.append(new String(b, 0, n));
        }


        return out.toString();
    }

    private void storeAndroidId() {

        String android_id;




        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}