package pt.ua.engserv.safeguard.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.R.layout;
import pt.ua.engserv.safeguard.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ActivityPackagesSelector extends Activity {

	private ListView lvApps;
	private List<ApplicationStatus> appsBackup;
    private List<ApplicationStatus> appsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packages_selector);

        appsList = new LinkedList<ApplicationStatus>();
        appsBackup = new LinkedList<ApplicationStatus>();

        lvApps = (ListView) findViewById(R.id.listViewApps);

        getAppsList(appsList);

        matchWithSavedPrefs(appsList);

        backupAppsList(appsList);

        final ArrayAdapter<ApplicationStatus> adapter = new MyAppsArrayAdapter(this, appsList);

        lvApps.setAdapter(adapter);
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
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickbuttonSaveApps(View view) {

        /* Obter referência para o SharedPreferences*/
        SharedPreferences sharedPref =  getSharedPreferences(
                                            getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        /* Apagar as preferências anteriores */
        editor.remove(getString(R.string.allowed_apps));
        editor.commit();

        Set<String> allowedAppsSet = new TreeSet<String>();
        for(ApplicationStatus as : appsList) {

            if(as.isEnabled()) {

                allowedAppsSet.add(as.getLabel());
            }
        }
        
        /* Guardar as novas preferências */
        editor.putStringSet(getString(R.string.allowed_apps), allowedAppsSet);
        editor.commit();

        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();

        /* Volta à Activity anterior */
        onBackPressed();
    }


    private void getAppsList(List<ApplicationStatus> appsList) {

        final PackageManager pm = getPackageManager();

        /* Obter a lista de aplicações instaladas */
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {

            if(pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {

                String appLabel = pm.getApplicationLabel(packageInfo).toString();
                Drawable appIcon = pm.getApplicationIcon(packageInfo);
                String packageName = packageInfo.packageName;
                Intent appIntent = pm.getLaunchIntentForPackage(packageInfo.packageName);

                /* A aplicação está inacessível por omissão */
                ApplicationStatus appStatus = new ApplicationStatus(appLabel, appIcon, false, appIntent);

                appsList.add(appStatus);
            }
        }
    }

    /* Guarda estado original da lista de aplicações permitidas */
    private void backupAppsList(List<ApplicationStatus> appsList) {

        for(ApplicationStatus as : appsList) {

            appsBackup.add(as.clone());
        }
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

        for( ApplicationStatus as : appsList ) {

            if(allowedAppsSet.contains(as.getLabel())) {

                as.setEnabled(true);
            }
        }
    }

}
