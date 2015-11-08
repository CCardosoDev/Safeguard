package pt.ua.engserv.safeguard.parental.config;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import pt.ua.engserv.safeguard.ActivityMain;
import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.R.id;
import pt.ua.engserv.safeguard.R.layout;
import pt.ua.engserv.safeguard.R.menu;
import pt.ua.engserv.safeguard.R.string;
import pt.ua.engserv.safeguard.messaging.Operation;
import pt.ua.engserv.safeguard.parental.ActivityLauncherWarning;
import pt.ua.engserv.safeguard.settings.ApplicationStatus;
import pt.ua.engserv.safeguard.settings.MyAppsArrayAdapter;
import pt.ua.engserv.safeguard.utils.Child;
import pt.ua.engserv.safeguard.utils.Globals;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityWelcome extends FragmentActivity {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private GoogleCloudMessaging gcm;
	private String gcmId;
	private String SENDER_ID = "873747395138";
    private int msgId = 1;
	private ProgressDialog ringProgressDialog;
	private static boolean registed = false;
	private String pin = "";
	private EditText etPin;
	private ListView lvChild;
	private List<Child> childList;
	private ListView lvApps;
	private List<ApplicationStatus> appsBackup;
    private List<ApplicationStatus> appsList;
	private int selectedChildId;
	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_welcome);
		
		setupActionBar();
		
		childList = new LinkedList<Child>();
		appsList = new LinkedList<ApplicationStatus>();
        appsBackup = new LinkedList<ApplicationStatus>();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mViewPager.setCurrentItem(getIntent().getIntExtra("step", 0));
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

	        @Override
	        public void onPageSelected(int arg0) {
	           
	        	Log.d("page", "page selected -> " + arg0);
	        	
	        	if(arg0 == 1 && registed) {
	        		
	        		mViewPager.setCurrentItem(2);
	        	}
	        	else if(arg0 == 2 && !registed) {
        			
        			registerDeviceOnGcm();
        			
        			launchRingDialog();
        		}
        		else if(arg0 == 3) {
        			
        			lvChild = (ListView) findViewById(R.id.listViewCriancas);
        			
        			/* obter lista das crianças */
        			getAndSetChildList();
        			
        			lvChild.setClickable(true);
        			lvChild.setOnItemClickListener( new AdapterView.OnItemClickListener() {
        				
        				@Override
        				  public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

        					Log.d("debug", "position:" + position + "  id:" + arg3);
        					
        				    Child child = (Child) lvChild.getItemAtPosition(position);
        				    
    				    	selectedChildId = child.getId();
        				    
    				    	for(int i = 0; i < childList.size(); i++) {
    				    		
    				    		try {
    				    			lvChild.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
    				    		}
    				    		catch(NullPointerException ex) {
    				    			Log.d("debug", "exception@" + i);
    				    		}
    				    	}
    				    	
    				    	try {
    				    		view.setBackgroundColor(Color.rgb(54, 193, 112));
    				    	}
    				    	catch(NullPointerException ex) {
    				    	}
        				  }
					});
        		}
        		else if(arg0 == 4) {
        			
        			lvApps = (ListView) findViewById(R.id.listViewAppsF);
        			
        			lvApps.setClickable(true);
        			
        			getAppsList(appsList);

        	        matchWithSavedPrefs(appsList);

        	        backupAppsList(appsList);

        	        final ArrayAdapter<ApplicationStatus> adapter = new MyAppsArrayAdapter(getApplicationContext(), appsList);

        	        lvApps.setAdapter(adapter);
        		}
        		else if(arg0 == 5) {
        			
        			etPin = (EditText) findViewById(R.id.editTextPin);
        			
        			etPin.addTextChangedListener(new TextWatcher() {
        				
        				@Override
        				public void onTextChanged(CharSequence s, int start, int before, int count) {
        					// TODO Auto-generated method stub
        					
        				}
        				
        				@Override
        				public void beforeTextChanged(CharSequence s, int start, int count,
        						int after) {
        					// TODO Auto-generated method stub
        					
        				}
        				
        				@Override
        				public void afterTextChanged(Editable s) {
        					 
        					String aux = s.toString();
        					LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout2);
        					
        					if(aux.length() == 4) {
        						
        						pin = aux;
        						
        						ll.setVisibility(View.VISIBLE);
        					}
        					else {
        						
        						pin = "";
        						
        						ll.setVisibility(View.INVISIBLE);
        					}
        					
        				}
        			});
        		}
        		else if(arg0 == 6 && pin.length() != 4) {
        			
        			Toast.makeText(getApplicationContext(), "Introduza um PIN válido", Toast.LENGTH_LONG).show();
        			mViewPager.setCurrentItem(4);
        		}
        		else if(arg0 == 6 && pin.length() == 4) {
        			
        			/* Guarda o PIN */
        			SharedPreferences sharedPref =  getSharedPreferences(
        	                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        	        SharedPreferences.Editor editor = sharedPref.edit();
        	        
        	        editor.putString("pin", pin);
        	        
        	        editor.commit();
        		}
	        }
	        

			@Override
	        public void onPageScrolled(int arg0, float arg1, int arg2) {
	            
	        }

	        @Override
	        public void onPageScrollStateChanged(int arg0) {
	            
	        }
	    });
	}
	
	private void setupActionBar() {

//		try {
//			
//		   ViewConfiguration config = ViewConfiguration.get(this);
//		   Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
//		   
//		   if(menuKeyField != null) {
//			   
//		       menuKeyField.setAccessible(true);
//		       menuKeyField.setBoolean(config, false);
//		   }
//		} 
//		catch (Exception e) {
//			
//		   e.printStackTrace();
//		}
		
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_welcome, menu);
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
				
			case R.id.action_exit :
				
				//System.exit(0);
				exitApplication();
				
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onResume() {

        super.onResume();
    }
	
	@Override
	public void onBackPressed() {
		
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
	}
	
	private void exitApplication() {
		
		//clear();
		
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
    
    public void onClickbuttonSaveAppsF(View view) {

        /* Obter referência para o SharedPreferences*/
        SharedPreferences sharedPref =  getSharedPreferences(
                                            getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        /* Apagar as preferências anteriores */
        editor.remove(getString(R.string.allowed_apps));
        editor.commit();

        Set<String> allowedAppsSet = new HashSet<String>();
        for(ApplicationStatus as : appsList) {

            if(as.isEnabled()) {

                allowedAppsSet.add(as.getLabel());
            }
        }
        /* Guardar as novas preferências */
        editor.putStringSet(getString(R.string.allowed_apps), allowedAppsSet);
        editor.commit();

        Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show();

        /* Continua no passo seguinte */
        mViewPager.setCurrentItem(5);
    }
	
	private void getAndSetChildList() {
		
		childList.clear();
		
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {
            	
            	SharedPreferences sharedPref =  getSharedPreferences(
                        getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
                
            	String androidId = sharedPref.getString("androidId", "");
            	
            	String text = null;
            	CertificateFactory cf = null;
        		try {
        			cf = CertificateFactory.getInstance("X.509");
        		} catch (CertificateException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		InputStream caInput = null;
        		try {
        			
        			caInput = new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/Certificados/safeguard.redes-215.crt"));
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		Certificate ca = null;
        		try {
        		    ca = cf.generateCertificate(caInput);
        		    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        		} catch (CertificateException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} finally {
        		    try {
        				caInput.close();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		try {

	        		// Create a KeyStore containing our trusted CAs
	        		String keyStoreType = KeyStore.getDefaultType();
	        		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
	        		keyStore.load(null, null);
	        		keyStore.setCertificateEntry("ca", ca);
	
	        		// Create a TrustManager that trusts the CAs in our KeyStore
	        		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
	        		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
	        		tmf.init(keyStore);
	
	        		// Create an SSLContext that uses our TrustManager
	        		SSLContext context = SSLContext.getInstance("TLS");
	        		context.init(null, tmf.getTrustManagers(), null);
	
	        		// Tell the URLConnection to use a SocketFactory from our SSLContext
	        		URL url = new URL("https://192.168.8.217:7706/PofilesService/GetProfiles?token=" + Globals.token);
	        		HttpsURLConnection urlConnection =
	        		    (HttpsURLConnection)url.openConnection();
	        		urlConnection.setHostnameVerifier(new HostnameVerifier() {
	        			
	        			@Override
	        			public boolean verify(String hostname, SSLSession session) {
	        				// TODO Auto-generated method stub
	        				return true;
	        			}
	        		});
	        		urlConnection.setSSLSocketFactory(context.getSocketFactory());
	        		InputStream in = urlConnection.getInputStream();
	        		
	        		StringBuffer out = new StringBuffer();
	                int n = 1;
	                while (n>0) {
	                    byte[] b = new byte[4096];
	                    n =  in.read(b);
	
	
	                    if (n>0) out.append(new String(b, 0, n));
	                }
	                
	                text = out.toString();
	                Log.d("REST_WS", out.toString());
        		}
        		catch(Exception ex) {
        			
        			ex.printStackTrace();
        		}
                
                return text;
            	
            }

            protected void onPostExecute(String results) {

                if (results != null) {
                    
                    try {
						JSONArray tmpArray = new JSONArray(results);
						
						for(int i = 0; i < tmpArray.length(); i++) {
							
							JSONObject obj = tmpArray.getJSONObject(i);
							
							childList.add(new Child(obj.getInt("profileID"), obj.getString("fullName"), obj.getString("photoLink")));
						}
						
						/* Ordena por nome */
						
						final ArrayAdapter<Child> adapter = new ChildArrayAdapter(getApplicationContext(), childList);
	        			
	        			lvChild.setAdapter(adapter);
						
					} catch (JSONException e) {
						
						e.printStackTrace();
					}
                }
            }
        };

        task.execute(null, null, null);
	}
	
	public void onClickButtonUsar(View view) {
		
		setAndroidIdToChild();
		
		Intent goToLauncher = new Intent(this, ActivityMain.class);
		startActivity(goToLauncher);
	}

	private void setAndroidIdToChild() {
		
		/* Guarda o childId */
		SharedPreferences sharedPref =  getSharedPreferences(
                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		Log.d("child", selectedChildId + "");
		
		editor.putInt("profileId", selectedChildId);
		editor.putBoolean("configCompleted", true);
		editor.commit();
		
		Log.d("profileId", sharedPref.getInt("profileId", -1) + "");
		
		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {
            	
            	SharedPreferences sharedPref =  getSharedPreferences(
                        getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
                
            	String token = sharedPref.getString("token", Globals.token);
            	String androidId = sharedPref.getString("androidId", "");
            	String compositionSecureAddress = getResources().getString(R.string.profiles_server_address);
            	
            	
            	CertificateFactory cf = null;
        		try {
        			cf = CertificateFactory.getInstance("X.509");
        		} catch (CertificateException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		InputStream caInput = null;
        		try {
        			
        			caInput = new BufferedInputStream(new FileInputStream(Environment.getExternalStorageDirectory().toString() + "/Certificados/safeguard.redes-215.crt"));
        		} catch (FileNotFoundException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		Certificate ca = null;
        		try {
        		    ca = cf.generateCertificate(caInput);
        		    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        		} catch (CertificateException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		} finally {
        		    try {
        				caInput.close();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
        		
        		try {

        			// Create a KeyStore containing our trusted CAs
        			String keyStoreType = KeyStore.getDefaultType();
        			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        			keyStore.load(null, null);
        			keyStore.setCertificateEntry("ca", ca);
        	
        			// Create a TrustManager that trusts the CAs in our KeyStore
        			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        			tmf.init(keyStore);
        	
        			// Create an SSLContext that uses our TrustManager
        			SSLContext context = SSLContext.getInstance("TLS");
        			context.init(null, tmf.getTrustManagers(), null);
        	
        			String urlParameters = "{\"token\":\"" + token + "\" , \"profileID\": " + selectedChildId + " , \"androidID\" : \"" + androidId + "\"}";
        			
        			// Tell the URLConnection to use a SocketFactory from our SSLContext
        			URL url = new URL(compositionSecureAddress + "PofilesService/InsertAndroidID");
        			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        			connection.setHostnameVerifier(new HostnameVerifier() {
        				
        				@Override
        				public boolean verify(String hostname, SSLSession session) {
        					
        					return true;
        				}
        			});
        			connection.setSSLSocketFactory(context.getSocketFactory());
        			
        			connection.setDoOutput(true);
        			connection.setDoInput(true);
        			connection.setRequestMethod("POST"); 
        			connection.setRequestProperty("Content-Type", "application/json"); 
        			connection.setRequestProperty("charset", "utf-8");
        			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

        			connection.connect();
        			
        			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
        			wr.writeBytes(urlParameters);
        			wr.flush();
        			wr.close();
        		
        			InputStream is;
        	        if (connection.getResponseCode() == 201) {
        	            is = connection.getInputStream();
        	        } else {
        	            is = connection.getErrorStream();
        	        }
        	        
        	        connection.disconnect();
        		}
        		catch(Exception e) {
        			
        			e.printStackTrace();
        		}
                
                return null;
            }

            protected void onPostExecute(String results) {

            }
        };

        task.execute(null, null, null);
		
	}



	public void onClickButtonSair(View view) {
		
		/* Envia o androidId para a Composição */
		setAndroidIdToChild();
		
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
	
	private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
        String registrationId = prefs.getString("gcmId", "");
        if (registrationId.isEmpty()) {
            Log.i("GCM", "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt("appVersion", Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("GCM", "App version changed.");
            return "";
        }
        
        return registrationId;
    }
	
	private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
	
	private void registerDeviceOnGcm() {
		
    	gcm = GoogleCloudMessaging.getInstance(this);
        gcmId = getRegistrationId(getApplicationContext());
        
        if (gcmId.isEmpty()) {
            registerInBackground();
        }
	}
	
	private void registerInBackground() {
        
    	AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    gcmId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + gcmId;

                    
                    final SharedPreferences prefs = getSharedPreferences(
                        getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
	            	SharedPreferences.Editor editor = prefs.edit();
	                
	                editor.putString("gcmId", gcmId);
	                
	                editor.commit();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            	registerDevice();
            	Log.d("GCM", msg);
            }
        };
        
        task.execute(null, null, null);
    }
	
	private void registerDevice() {

		AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... objects) {
            	
            	SharedPreferences sharedPref =  getSharedPreferences(
                        getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
                
            	String androidId = sharedPref.getString("androidId", "");
            	String gcmId = sharedPref.getString("gcmId", "");
            	
            	String backofficeAddress = getResources().getString(R.string.backoffice_address);
            	
                String URL = backofficeAddress + "webresources/Registration/"
                                + androidId + "/" + gcmId;

                Log.d("token", URL);
                
                HttpClient httpClient = new DefaultHttpClient();
                HttpContext localContext = new BasicHttpContext();
                HttpGet httpGet = new HttpGet(URL);
                String text = null;
                try {
                    HttpResponse response = httpClient.execute(httpGet, localContext);

                    HttpEntity entity = response.getEntity();

                    text = getASCIIContentFromEntity(entity);
                    Log.d("token", "registado!\n" + text);
                } catch (Exception e) {
                	Log.d("token", "erro");
                	e.printStackTrace();
                    return e.getLocalizedMessage();
                    
                }
                
                return text;
            }

            protected void onPostExecute(String results) {

                if (results!=null) {

                    Log.d("REST_WS", results);
                    
                    String backofficeAddress = getResources().getString(R.string.backoffice_address);
                    String loginSecureAdress = getResources().getString(R.string.login_server_address);
                    String redirectUrl = "";
					
                    try {
						redirectUrl = URLEncoder.encode(backofficeAddress + "webresources/Registration/updateToken/" +
								Globals.androidId + "/", "UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
                    
                    String url = loginSecureAdress +
									"/login?redirecturl=" + redirectUrl
									+ "&androidid=" + Globals.androidId;
                    
                    Log.d("URL", url);
                    
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    ringProgressDialog.dismiss();
                    
                    startActivity(browserIntent);
                }
            }
        };

        task.execute(null, null, null);
    }
	
	private String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        
		InputStream in = entity.getContent();

        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);


            if (n>0) out.append(new String(b, 0, n));
        }

        return out.toString();
    }
	
	private void sendPingToGCM() {
    	
    	gcm = GoogleCloudMessaging.getInstance(this);
    	
    	AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                        data.putString("operation", Operation.PING.toString());
                        String id = Integer.toString(msgId++);
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
                        msg = "Sent message";
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d("GCM", "PING enviado !!!");
            }
        };
        
        task.execute(null, null, null);
    }
	
	private void launchRingDialog() {
    	
	    ringProgressDialog = ProgressDialog.show(ActivityWelcome.this, "Por favor aguarde", "A ligar ao servidor", true);
	
	    ringProgressDialog.setCancelable(false);
    }
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {

			// Show 7 total pages.
			return 7;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
				
				case 0:
					
					return getString(R.string.welcome_title_section_1);
				
				case 1:
				
					return getString(R.string.welcome_title_section_2);
					
				case 2:
					
					return getString(R.string.welcome_title_section_3);
					
				case 3:
					
					return getString(R.string.welcome_title_section_4);
					
				case 4:
	
					return getString(R.string.welcome_title_section_5);
					
				case 5:
					
					return getString(R.string.welcome_title_section_6);
					
				case 6:
					
					return getString(R.string.welcome_title_section_7);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = null;
			
			if(getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
				
				rootView = inflater.inflate(R.layout.fragment_activity_welcome_1, container, false);
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER) == 2) {
				
				rootView = inflater.inflate(R.layout.fragment_activity_welcome_2, container, false);
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER) == 3) {
				
				rootView = inflater.inflate(R.layout.fragment_activity_welcome_3, container, false);
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER) == 4) {

				rootView = inflater.inflate(R.layout.fragment_activity_welcome_4, container, false);
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER) == 5) {
				
				rootView = inflater.inflate(R.layout.fragment_activity_welcome_5, container, false);
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER) == 6) {
				
				rootView = inflater.inflate(R.layout.fragment_activity_welcome_6, container, false);
			}
			else if(getArguments().getInt(ARG_SECTION_NUMBER) == 7) {
				
				rootView = inflater.inflate(R.layout.fragment_activity_welcome_7, container, false);
			}
			
			return rootView;
		}
	}
	
	public static void setRegisted(boolean b) {
		
		registed = b;
	}
}
