package pt.ua.engserv.safeguard.parental;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.R.layout;
import pt.ua.engserv.safeguard.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class ActivityLock extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_lock);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_lock, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		
	}

}
