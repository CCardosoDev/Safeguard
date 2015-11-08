package pt.ua.engserv.safeguard;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.camera.CameraService;
import pt.ua.engserv.safeguard.messaging.Operation;
import pt.ua.engserv.safeguard.parental.ActivityLock;
import pt.ua.engserv.safeguard.parental.ActivityPoke;
import pt.ua.engserv.safeguard.parental.config.ActivityWelcome;
import pt.ua.engserv.safeguard.utils.Globals;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    String TAG = "GCM";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	Log.d("GCM", "A analisar mensagem...");
    	Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
        	
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

            	if(extras.containsKey("operation")) {
            		
            		String operation = extras.getString("operation");
            		Log.d("GCM", "operation: " + operation);
            		
            		if(operation.equalsIgnoreCase(Operation.TOKEN_OFFER.toString())) {
            			
            			// Obtém o token da mensagem
                		String myToken = extras.getString("token");
                		
                		// Guarda o token
                		SharedPreferences sharedPref =  getSharedPreferences(
                                getString(R.string.sharedPref_allowed_apps), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        
                        editor.putString("token", myToken);
                        
                        Globals.token = myToken;
                        
                        editor.commit();
                		
                		sendNotification("Token: " + extras.getString("token"));
                		
                		// Release the wake lock provided by the WakefulBroadcastReceiver.
                        GcmBroadcastReceiver.completeWakefulIntent(intent);
                        
                        ActivityWelcome.setRegisted(true);
                        
                        Intent goToChildSelect = new Intent(getApplicationContext(), ActivityWelcome.class);
                        goToChildSelect.putExtra("step", 2);
                		goToChildSelect.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                		startActivity(goToChildSelect);
            		}
            		else if(operation.equalsIgnoreCase(Operation.LOCK.toString())) {
            			
            			// Release the wake lock provided by the WakefulBroadcastReceiver.
                        GcmBroadcastReceiver.completeWakefulIntent(intent);
                        
                        ActivityMain.setLocked(true);
                        
                        Intent goToLock = new Intent(getApplicationContext(), ActivityLock.class);
                        goToLock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(goToLock);
                        
            		}
        			else if(operation.equalsIgnoreCase(Operation.UNLOCK.toString())) {
            			
            			// Release the wake lock provided by the WakefulBroadcastReceiver.
                        GcmBroadcastReceiver.completeWakefulIntent(intent);
                        
                        ActivityMain.setLocked(false);
                        
                        Intent goToLauncher = new Intent(getApplicationContext(), ActivityMain.class);
                		goToLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                		startActivity(goToLauncher);
            		}
        			else if(operation.equalsIgnoreCase(Operation.PHOTO_REQUEST.toString())) {
        				
        				// Release the wake lock provided by the WakefulBroadcastReceiver.
                        GcmBroadcastReceiver.completeWakefulIntent(intent);
                        
                        String requestId = extras.getString("requestId");
                        
                        Log.d("camera", requestId + "");
                        
                        Intent photoService = new Intent(this, CameraService.class);
                        photoService.putExtra("requestId", requestId);
                        
                        startService(photoService);
        			}
        			else if(operation.equalsIgnoreCase(Operation.CALL_REQUEST.toString())) {
        				
        				// Release the wake lock provided by the WakefulBroadcastReceiver.
                        GcmBroadcastReceiver.completeWakefulIntent(intent);
                        
                        String number = "tel:" + extras.getString("phoneNumber");
                        
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(callIntent);
        			}
        			else if(operation.equalsIgnoreCase(Operation.POKE_REQUEST.toString())) {
        				
        				// Release the wake lock provided by the WakefulBroadcastReceiver.
                        GcmBroadcastReceiver.completeWakefulIntent(intent);
                        
                        String requestId = extras.getString("requestId");
                        
                        Intent poke = new Intent(getApplicationContext(), ActivityPoke.class);
                        poke.putExtra("requestId", requestId);
                        poke.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(poke);
        			}
            		
            		
            	}
            	else {
            		
            		Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
	                // Post notification of received message.
	                sendNotification("Received: " + extras.toString());
	                Log.i(TAG, "Received: " + extras.toString());
            	}
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ActivityMain.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_launcher)
        .setContentTitle("GCM Notification")
        .setDefaults(Notification.DEFAULT_SOUND)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
