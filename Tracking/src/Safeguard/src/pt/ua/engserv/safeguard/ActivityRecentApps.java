package pt.ua.engserv.safeguard;

import java.util.Timer;
import java.util.TimerTask;

import pt.ua.engserv.safeguard.utils.MyActivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class ActivityRecentApps extends Activity {

	private TextView tvAppsLeft;
	private MyActivityManager mam;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recent_apps);
		
		mam = new MyActivityManager(this);
		
		tvAppsLeft = (TextView) findViewById(R.id.tvAppsLeft);
		tvAppsLeft.setText("" + (mam.numberOfRecentTasks() - 2));
		
		//Declare the timer
		Timer t = new Timer();
		//Set the schedule function and rate
		t.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		        
		    	if((mam.numberOfRecentTasks() - 2) == 0) {
		    		
		    		Intent goToMainActivity = new Intent(getApplicationContext(), ActivityMain.class);
		    		startActivity(goToMainActivity);
		    	}
		    	
		    	//Called each time when 1000 milliseconds (1 second) (the period parameter)
		    	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						tvAppsLeft.setText("" + (mam.numberOfRecentTasks() - 2));
					}
				});
		    }
		         
		},
		//Set how long before to start calling the TimerTask (in milliseconds)
		1000,
		//Set the amount of time between each execution (in milliseconds)
		1000);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_recent_apps, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		
		
	}

}
