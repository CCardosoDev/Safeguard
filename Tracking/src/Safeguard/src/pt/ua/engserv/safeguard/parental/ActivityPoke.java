package pt.ua.engserv.safeguard.parental;

import android.os.Bundle;
import android.app.Activity;

public class ActivityPoke extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String requestId = getIntent().getExtras().getString("requestId");
		
		Poke p = new Poke(this, requestId);
		p.show();
	}
	
}
