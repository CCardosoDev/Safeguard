package pt.ua.engserv.safeguard.parental;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.R.layout;
import pt.ua.engserv.safeguard.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;


public class ActivityLauncherWarning extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_warning);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_launcher_warning, menu);
        return true;
    }



    public void showHomeLauncherChooser(View view) {

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

}