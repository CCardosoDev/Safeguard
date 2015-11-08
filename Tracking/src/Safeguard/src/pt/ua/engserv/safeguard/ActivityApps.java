package pt.ua.engserv.safeguard;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import pt.ua.engserv.safeguard.settings.ActivityPackagesSelector;
import pt.ua.engserv.safeguard.settings.ActivitySettings;
import pt.ua.engserv.safeguard.settings.ApplicationStatus;
import pt.ua.engserv.safeguard.utils.AppCellAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class ActivityApps extends Activity {

	private GridView gridView;
	private List<ApplicationStatus> appsList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps);
		
		/* Setup ActionBar */
		setupActionBar();
		
		appsList = new LinkedList<ApplicationStatus>();
		
		getAllowedAppsList(appsList);
		
		gridView = (GridView) findViewById(R.id.gridViewApps);
		
		gridView.setAdapter(new AppCellAdapter(getApplicationContext(), appsList));
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				
				Intent goToAppIntent = null;
				String selectedApp = ((TextView) v.findViewById(R.id.app_name)).getText().toString();
				
				for(ApplicationStatus as : appsList) {
					
					if(as.getLabel().equalsIgnoreCase(selectedApp)) {
						
						goToAppIntent = as.getLauncherIntent();
						break;
					}
				}
				
				Intent goToHome = new Intent(getApplicationContext(), ActivityMain.class);
				startActivity(goToHome);
				
				startActivity(goToAppIntent);
			}
		});
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_apps, menu);
		return true;
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
	
	private void clear() {

        PackageManager p = getApplicationContext().getPackageManager();

        try {

            p.clearPackagePreferredActivities(getPackageName());
        }
        catch (NullPointerException ex) {
        }
    }
	
	private void getAllowedAppsList(List<ApplicationStatus> appsList) {

        final PackageManager pm = getPackageManager();

        /* Obter a lista de aplicações instaladas */
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if(pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {

                String appLabel = pm.getApplicationLabel(packageInfo).toString();
                Drawable appIcon = pm.getApplicationIcon(packageInfo);
                String packageName = packageInfo.packageName;
                Intent launcherIntent = pm.getLaunchIntentForPackage(packageName);
            	
                /* A aplicação está inacessível por omissão */
                ApplicationStatus appStatus = new ApplicationStatus(appLabel, appIcon, false, launcherIntent);

                appsList.add(appStatus);
            }
        }
        
        matchWithSavedPrefs(appsList);
    }
	
	private void matchWithSavedPrefs(List<ApplicationStatus> appsList) {

        /* Obter referência para o SharedPreferences*/
        SharedPreferences sharedPref =  getSharedPreferences(
                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);

        Set<String> allowedAppsSet;

        allowedAppsSet = sharedPref.getStringSet(getString(R.string.allowed_apps), null);

        if( allowedAppsSet == null) {

            return;
        }

        for(int i = 0; i < appsList.size(); i++) {
        	
        	ApplicationStatus as = appsList.get(i);
            
        	if(!allowedAppsSet.contains(as.getLabel())) {

                appsList.remove(as);
                i--;
            }
        }
      
        /* Ordenar pelo nome */
        Collections.<ApplicationStatus>sort(appsList);
    }

}
