package pt.ua.engserv.safeguard;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class ActivityLogin extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	public void login(View view) {
		
		SharedPreferences prefs = getSharedPreferences(
                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		
		//registerDevice();
		
		editor.putString("token", "aa");
		
		editor.commit();
		
		Log.d("token", prefs.getString("token", "null"));
		
		Intent goToLauncher = new Intent(this, ActivityMain.class);
		startActivity(goToLauncher);
	}

	
	
}
