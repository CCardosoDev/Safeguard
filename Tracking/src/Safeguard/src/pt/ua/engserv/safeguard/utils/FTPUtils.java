package pt.ua.engserv.safeguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import pt.ua.engserv.safeguard.ActivityMain;
import pt.ua.engserv.safeguard.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class FTPUtils {
	
	public static void uploadPictureToServer(final File file, final String fileName, final String requestId) {
		
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {

			boolean retry = false;
			
			@Override
			protected String doInBackground(String... arg0) {
				
				FTPClient con = null;
				boolean result = false;
				retry = !retry;
		        
		        try
		        {
		            con = new FTPClient();
		            con.connect(Globals.FTP_SERVER_ADDRESS);
		            
		            Log.d("ftp", "ligou");
		            
		        	if (con.login(Globals.FTP_SERVER_USERNAME, Globals.FTP_SERVER_PASSWORD))
		            {
		        		Log.d("ftp", "logou");
		                con.enterLocalPassiveMode();
		                con.setFileType(FTP.BINARY_FILE_TYPE);
		                
		                FileInputStream in = new FileInputStream( file );
		                
		                result = con.storeFile("stuff/pictures/" + fileName, in);
		                Log.d("ftp", "picture");
		                
		                in.close();
		                
		                if (result) {
		                	
		                	Log.d("ftp", "sucesso");
		                	
		                	con.sendSiteCommand("chmod 644 stuff/pictures/" + fileName);
		                	
		                }
		                con.logout();
		                con.disconnect();
		            }
		        	else {
		        		
		        		Log.d("ftp", "erro ligação");
		        	}
		        }
		        catch (Exception e) {
		        	
		            e.printStackTrace();
		        }
		        
		       return "ok"; 
			}
			
			@Override
            protected void onPostExecute(String msg) {
                
				// Se falhar tenta de novo
				if(!msg.contains("ok") && retry) {
					
					doInBackground((String[]) null);
				}
				else {
					
					informBackofficeAboutPicture(requestId);
				}
            }

			private void informBackofficeAboutPicture(final String requestId) {
				
				AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
		            @Override
		            protected String doInBackground(Void... objects) {
		            	
		            	String token = Globals.token;
		            	String androidId = Globals.androidId;
		            	
		            	String backofficeAddress = Globals.backofficeAddress;
		            	
		            	String photoURL = "http://" + Globals.FTP_SERVER_ADDRESS + "/safeguard/pictures/" + fileName;
		            	
		            	Log.d("ftp", "photoURL: " + photoURL);
		            	
		            	String encodedURL = "";
						try {
							
							encodedURL = URLEncoder.encode(photoURL, "UTF-8");
						} 
						catch (UnsupportedEncodingException e1) {
							
							e1.printStackTrace();
						}
		            	
		                String URL = backofficeAddress + "webresources/photo/offer/"
		                                + token + "/" + androidId + "/" + requestId + "/" + encodedURL;
	
		                Log.d("ftp", "backoffice WS: " + URL);
		                
		                Log.d("ftp", URL);
		                
		                HttpClient httpClient = new DefaultHttpClient();
		                HttpContext localContext = new BasicHttpContext();
		                HttpGet httpGet = new HttpGet(URL);
		                String text = null;
		                try {
		                    HttpResponse response = httpClient.execute(httpGet, localContext);
	
		                    HttpEntity entity = response.getEntity();
	
		                    text = getASCIIContentFromEntity(entity);
		                    
		                    Log.d("ftp", "photo offer: " + text);
		                } 
		                catch (Exception e) {
		                	Log.d("ftp", "erro");
		                	e.printStackTrace();
		                    return e.getLocalizedMessage();
		                    
		                }
		                
		                return text;
		            }
	
		            protected void onPostExecute(String results) {
	
		                
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
		        };
		        
		        task.execute(null, null, null);
			}
				
			
		};
		
		task.execute(null, null, null);
	}

}
