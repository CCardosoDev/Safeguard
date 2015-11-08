package pt.ua.engserv.safeguard;

import java.util.Date;
import java.util.Locale;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import pt.ua.engserv.safeguard.gps.ServiceTracker;
import pt.ua.engserv.safeguard.parental.ActivityLauncherWarning;
import pt.ua.engserv.safeguard.parental.ActivityLock;
import pt.ua.engserv.safeguard.parental.config.ActivityWelcome;
import pt.ua.engserv.safeguard.settings.ActivityPackagesSelector;
import pt.ua.engserv.safeguard.settings.ActivitySettings;
import pt.ua.engserv.safeguard.utils.Globals;
import pt.ua.engserv.safeguard.utils.MyActivityManager;

import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * @author luis
 *
 */
public class ActivityMain extends Activity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationManager locationManager;
    private String provider;
    private String androidId;
    private static Context context;
    private Handler timeHandler;
    private TextView tvHora;
    private static TextView tvMorada;
    private TextView tvDia;
    private static boolean isLocked;
    private static boolean configCompleted;
    private boolean firstRun;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        firstRun = true;
        	
        /* remove o home logo da action bar */
        setupActionBar();
        
        /* obtém referências para os elementos gráficos do layout */
        tvHora = (TextView) findViewById(R.id.horas);
        tvDia = (TextView) findViewById(R.id.diaSemana);
        tvMorada = (TextView) findViewById(R.id.tvmorada);
        
        timeHandler = new Handler();
        
        context = getAppContext();
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        storeAndroidId();
        
        SharedPreferences sharedPref =  getSharedPreferences(
              getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        Globals.token = sharedPref.getString("token", "");
        
        Globals.backofficeAddress = getResources().getString(R.string.backoffice_address);
        
        /* por omissão a aplicação não está bloqueada */
        isLocked = false;
        
        /* por omissão a configuração não está completa */
        editor.putBoolean("configCompleted", false);
        editor.commit();
        configCompleted = false;
		
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
    	// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_apps, menu);
        return true;
    }

    @Override
    public void onResume() {

        super.onResume();
        
        SharedPreferences sharedPref =  getSharedPreferences(
                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        configCompleted = sharedPref.getBoolean("configCompleted", false);
        
        // Verifica se os Google Play Services estão disponíveis
        if(!checkPlayServices()) {
        	
        	System.exit(-1);
        }
        
        /* Verifica se existe um token */
        if(Globals.token.equalsIgnoreCase("")) {
        	
        	Intent goToWelcome = new Intent(this, ActivityWelcome.class);
        	goToWelcome.putExtra("step", 0);
        	startActivity(goToWelcome);
        }
        /* Verifica se a aplicação está completamente configurada */
        else if(!configCompleted) {
        	
        	Intent goToWelcome = new Intent(this, ActivityWelcome.class);
        	goToWelcome.putExtra("step", 2);
        	startActivity(goToWelcome);
        }
        /* Verifica se o launcher por omissão é esta aplicação */
        else if( !getCurrentLauncher().contains("safeguard") ) {

            Intent launcherWarning = new Intent(this, ActivityLauncherWarning.class);
            startActivity(launcherWarning);
        }
//        else if(firstRun) {
//        	
//        	firstRun = false;
//        	
//        	/* pede ao utilizador que elimine as apps abertas recentemente */
//            if(new MyActivityManager(this).numberOfRecentTasks() > 2) {
//            	
//            	Intent goToDeleteRecentApps = new Intent(this, ActivityRecentApps.class);
//            	startActivity(goToDeleteRecentApps);
//            }
//        }
        /* A aplicação está pronta a correr */
        else {
        	
        	updateTime.run();
        	
        	Log.d("gps", "onResume() -> iniciar o GPS");
        	startGPSTracking(null);
        	
        	if(isLocked) {
        		
        		Intent goToLock = new Intent(this, ActivityLock.class);
        		startActivity(goToLock);
        	}
        }
        
        	
    }


	@Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onBackPressed() {

        Log.d("onBackPressed()", "back button pressed!");
    }
    
    private void setupActionBar() {

		try {
			
		   ViewConfiguration config = ViewConfiguration.get(this);
		   Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
		   
		   if(menuKeyField != null) {
			   
		       menuKeyField.setAccessible(true);
		       menuKeyField.setBoolean(config, false);
		   }
		} 
		catch (Exception e) {
			
		   e.printStackTrace();
		}
    	
		/* remove o home logo da action bar */
        getActionBar().setDisplayShowHomeEnabled(false);

	}
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			
			case R.id.action_allowedApps :
				
				goToPackagesSelector();
				
				return true;
				
			case R.id.action_exit :
				
				exitApplication();
				
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
    
    private void goToPackagesSelector() {
    	
    	LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View pinDialog = li.inflate(R.layout.dialog_pin, null);
        
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(pinDialog);
        
        final EditText etPIN = (EditText) pinDialog.findViewById(R.id.editTextPinD);
		
        alertDialogBuilder.setCancelable(true)
        					.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									dialog.cancel();
								}
							})
							.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									SharedPreferences sharedPref =  getSharedPreferences(
				        	                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
									
									String originalPin = sharedPref.getString("pin", "333");
									String inputPin = etPIN.getText().toString();
									
									if(inputPin.equals(originalPin)) {
										
										Intent goToAppsSelector = new Intent(getApplicationContext(), ActivityPackagesSelector.class);
										startActivity(goToAppsSelector);
									}
									else {
										
										Toast.makeText(getApplicationContext(), "PIN incorreto!", Toast.LENGTH_SHORT).show();
									}
								}
							});
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        
        try {
        	alertDialog.show();
        }
        catch(Exception ex) {
        	
        	ex.printStackTrace();
        }
    }
    
    private void exitApplication() {
		
		LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View pinDialog = li.inflate(R.layout.dialog_pin, null);
        
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(pinDialog);
        
        final EditText etPIN = (EditText) pinDialog.findViewById(R.id.editTextPinD);
		
        alertDialogBuilder.setCancelable(true)
        					.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									dialog.cancel();
								}
							})
							.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									SharedPreferences sharedPref =  getSharedPreferences(
				        	                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
									
									String originalPin = sharedPref.getString("pin", "XXXXX");
									String inputPin = etPIN.getText().toString();
									
									if(inputPin.equals(originalPin)) {
										
										clear();
										
										Intent goToLauncher = new Intent(getApplicationContext(), ActivityMain.class);
										startActivity(goToLauncher);

								        Context c = getApplicationContext();

								        PackageManager pm = c.getPackageManager();
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
									else {
										
										Toast.makeText(getApplicationContext(), "PIN incorreto!", Toast.LENGTH_SHORT).show();
									}
								}
							});
        
        AlertDialog alertDialog = alertDialogBuilder.create();
        
        try {
        	alertDialog.show();
        }
        catch(Exception ex) {
        	
        	ex.printStackTrace();
        }
	}

    public void startGPSTracking(View view) {

        /* verificar se o GPS está ligado */
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        /* Se o GPS não está ligado, abre as definições de localização */
        if (!gpsEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); 
        criteria.setAltitudeRequired(false); 
        criteria.setBearingRequired(false); 
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_LOW); 
        provider = locationManager.getBestProvider(criteria, true);
    	
        Intent serviceTracker = new Intent(this, ServiceTracker.class);

        serviceTracker.putExtra("locationProvider", provider);

        startService(serviceTracker);
    }

    public void stopGPSTracking(View view) {

        stopService(new Intent(this, ServiceTracker.class));
    }
    
    public static void setLocation(String address) {
    	
    	tvMorada.setText(address);
    }
    
    public void goToMyApps(View view) {
    	
    	Intent goToApps = new Intent(this, ActivityApps.class);
    	startActivity(goToApps);
    }

    public void launcher(View view) {

        clear();

        Context c = getApplicationContext();

        PackageManager pm = c.getPackageManager();
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
    
    public static Context getAppContext() {
    	
    	return context;
    }

    public static boolean isLocked() {
    	
		return isLocked;
	}


	public static void setLocked(boolean locked) {
		
		isLocked = locked;
	}
	
	public static void setConfigCompleted(boolean configCompleted) {
		
		ActivityMain.configCompleted = configCompleted;
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        
        return true;
    }


    private void storeAndroidId() {
    	
    	androidId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        
        SharedPreferences sharedPref =  getSharedPreferences(
                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        editor.putString("androidId", androidId);
        
        Globals.androidId = androidId;
        
        editor.commit();
    }
    
    private Runnable updateTime = new Runnable() {
		
    	@Override
		public void run() {
			
    		Locale pt = new Locale("pt", "PT");
			DateFormat dateFormat = new SimpleDateFormat("HH:mm", pt);
			DateFormat weekDay = new SimpleDateFormat("EEEE,  dd:MMMM:yyyy", pt); 
			Date date = new Date();
			tvHora.setText(dateFormat.format(date));
			tvDia.setText(weekDay.format(date).replace(":", " de "));
		
			if(!ServiceTracker.isRunning()) {
				
				Log.d("gps", "updateTime() -> iniciar o GPS");
				startGPSTracking(null);
			}
			
			timeHandler.postDelayed(updateTime, 
	                DateUtils.MINUTE_IN_MILLIS - System.currentTimeMillis() % DateUtils.MINUTE_IN_MILLIS);
		}
	};
	
	
}
