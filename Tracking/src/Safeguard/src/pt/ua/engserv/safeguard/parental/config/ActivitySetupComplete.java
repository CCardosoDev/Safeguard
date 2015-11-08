package pt.ua.engserv.safeguard.parental.config;

import pt.ua.engserv.safeguard.ActivityMain;
import pt.ua.engserv.safeguard.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;

public class ActivitySetupComplete extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup_complete);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_setup_complete, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		
	}
	
	public void onClickButtonUsar(View view) {
		
		Intent goToLauncher = new Intent(this, ActivityMain.class);
		startActivity(goToLauncher);
	}

	public void onClickButtonSair(View view) {
		
		clear();
		
		Intent goToLauncher = new Intent(this, ActivityMain.class);
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
	
	private void clear() {

        PackageManager p = getApplicationContext().getPackageManager();

        try {

            p.clearPackagePreferredActivities(getPackageName());
        }
        catch (NullPointerException ex) {
        }
    }
	
}
