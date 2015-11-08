package pt.ua.engserv.safeguard.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import pt.ua.engserv.safeguard.utils.FTPUtils;
import pt.ua.engserv.safeguard.utils.FileType;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraService extends Service {
	
	private String requestId;
	
	public CameraService() {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		
		requestId = intent.getStringExtra("requestId");
		
		/* Após 5 segundos se não conseguir foto deve fechar */
		//Declare the timer
		final Timer t = new Timer();
		//Set the schedule function and rate
		t.scheduleAtFixedRate(new TimerTask() {

		    @Override
		    public void run() {
		        
		    	Log.d("camera", "serviço vai ser cancelado");
		    	
		    	t.cancel();
		    	System.gc();
		    	stopSelf();
		    }
		}, 
		//Set how long before to start calling the TimerTask (in milliseconds)
		5000,
		//Set the amount of time between each execution (in milliseconds)
		5000);
		
		SurfaceView mview = new SurfaceView(getBaseContext());
	    try {
	    	Log.d("CAMERA", "a tentar aceder à camera");
			
			Camera camera = null;
			
			if(camera == null)
				camera = Camera.open();
			
			Camera.Parameters parameters = camera.getParameters();
			
			/* tenta activar o flash */
			Log.d("CAMERA", parameters.getSupportedFlashModes().toString());
			if(parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
				
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			}
			
			Log.d("CAMERA", "com acesso à camera");
			camera.setParameters(parameters);
	    	
			//mview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
			//mview.getHolder().setSizeFromLayout();
			camera.setPreviewDisplay(mview.getHolder());
			//Thread.sleep(1100);
	        camera.startPreview();
	        camera.setPreviewCallback(null);
	        //Thread.sleep(500);
	        System.gc();
			camera.takePicture(null, null, photoCallback);
			
	    } catch (IOException ex) {
	        
	        ex.printStackTrace();
	    }
	    catch (Exception ex) {
	    	
	    	ex.printStackTrace();
	    }
		
		return Service.START_STICKY;
	}
	
Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		
	    public void onPictureTaken(byte[] data, Camera camera) {

	    	String path = Environment.getExternalStorageDirectory().toString() + "/safeguard";
            File myNewFolder = new File(path);
            
            if(!myNewFolder.exists()) {
            	
            	myNewFolder.mkdir();
            }
            
            path += "/pictures";
            myNewFolder = new File(path);
            if(!myNewFolder.exists()) {
            	
            	myNewFolder.mkdir();
            }
	    	
	    	Log.d("CAMERA", "foto tirada!");
	    	
	    	String picName = "pic_" + String.valueOf( System.currentTimeMillis() ) + ".jpg";
	    	File picture = new File( path, picName);
	    	Uri uriTarget = Uri.fromFile( picture );
	        OutputStream imageFileOS;

	        try {

	            imageFileOS = getContentResolver().openOutputStream(uriTarget);
	            imageFileOS.write(data);
	            imageFileOS.flush();
	            imageFileOS.close();
	            
	            Toast.makeText(getApplicationContext(), "Image saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();
	        }
	        catch (FileNotFoundException e) {
	        	
	            e.printStackTrace();
	        } 
	        catch (IOException e) {
	        	
	            e.printStackTrace();
	        }
	        
	        Log.d("CAMERA", "foto guardada!");
	        
	        camera.release();
	        
	        FTPUtils.uploadPictureToServer(picture, picName, requestId);
	    }
	};
	
	private void setCameraDisplayOrientation(Activity activity,
	         int cameraId, android.hardware.Camera camera) {
	     android.hardware.Camera.CameraInfo info =
	             new android.hardware.Camera.CameraInfo();
	     android.hardware.Camera.getCameraInfo(cameraId, info);
	     int rotation = activity.getWindowManager().getDefaultDisplay()
	             .getRotation();
	     int degrees = 0;
	     switch (rotation) {
	         case Surface.ROTATION_0: degrees = 0; break;
	         case Surface.ROTATION_90: degrees = 90; break;
	         case Surface.ROTATION_180: degrees = 180; break;
	         case Surface.ROTATION_270: degrees = 270; break;
	     }

	     int result;
	     if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	         result = (info.orientation + degrees) % 360;
	         result = (360 - result) % 360;  // compensate the mirror
	     } else {  // back-facing
	         result = (info.orientation - degrees + 360) % 360;
	     }
	     camera.setDisplayOrientation(result);
	 }
}
