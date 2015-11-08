package pt.ua.engserv.safeguard.parental;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.utils.Globals;
import pt.ua.engserv.safeguard.utils.PokeAnswer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Poke extends Dialog implements android.view.View.OnClickListener {

	private Activity activity;
	private Button btnGood, btnMl, btnBad;
	private String requestId;
	private PokeAnswer answer;

	public Poke(Activity a, String requestId) {
		
		super(a);
		
		this.activity = a;
		this.requestId = requestId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.poke_alert_dialog);
		
		btnGood = (Button) findViewById(R.id.btn_good);
		btnMl = (Button) findViewById(R.id.btn_ml);
		btnBad = (Button) findViewById(R.id.btn_bad);
		
		btnGood.setOnClickListener(this);
		btnMl.setOnClickListener(this);
		btnBad.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
			case R.id.btn_good:
				
				answer = PokeAnswer.GOOD;
				dismiss();
				
				break;
				
			case R.id.btn_ml:
				
				answer = PokeAnswer.MORELESS;
				dismiss();
				
				break;
				
			case R.id.btn_bad:
				
				answer = PokeAnswer.BAD;
				dismiss();
				
				break;
				
			default:
				
				dismiss();
				
				break;
		}
		
		/* enviar resposta à composição */
		task.execute(null, null, null);
		
		/* retirar a activity */
		activity.onBackPressed();
	}
	
	private AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
       
		@Override
        protected String doInBackground(Void... objects) {

        	SharedPreferences sharedPref =  activity.getSharedPreferences(
                    activity.getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        	
        	String token = sharedPref.getString("token", "");
        	
        	String backofficeAddress = activity.getResources().getString(R.string.backoffice_address);
        	
            String URL = backofficeAddress + "webresources/poking/offer/" + token + "/" + Globals.androidId + "/" + requestId + "/" + answer.toString();

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
    			
        		Log.d("poke", results);
            }
            else {

            }
        }
    };
    
    private String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

        InputStream in = entity.getContent();

        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {

            byte[] b = new byte[4096];
            n =  in.read(b);


            if (n>0) {

                out.append(new String(b, 0, n));
            }
        }

        return out.toString();
    }
}
