package pt.ua.engserv.safeguard.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import pt.ua.engserv.safeguard.R;
import pt.ua.engserv.safeguard.R.layout;
import pt.ua.engserv.safeguard.R.menu;
import pt.ua.engserv.safeguard.utils.FTPUtils;
import pt.ua.engserv.safeguard.utils.FileType;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.widget.Toast;

public class ActivityCamera extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		SurfaceView mview = new SurfaceView(getBaseContext());
	    try {
	    	Log.d("CAMERA", "a tentar aceder à camera");
			
			Camera camera = Camera.open();
			
			Camera.Parameters parameters = camera.getParameters();
			
			/* tenta activar o flash */
			Log.d("CAMERA", parameters.getSupportedFlashModes().toString());
			if(parameters.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_ON)) {
				
					parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			}
			
			Log.d("CAMERA", "com acesso à camera");
			//camera.setParameters(parameters);
	    	
	        camera.setPreviewDisplay(mview.getHolder());
	        camera.startPreview();
	        camera.setPreviewCallback(null);
	        System.gc();
			camera.takePicture(null,null, photoCallback);
			
			
	    } catch (IOException e) {
	        
	        e.printStackTrace();
	    }
	    catch (Exception ex) {
	    	
	    	ex.printStackTrace();
	    }
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_camera, menu);
		return true;
	}
	
Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
		
	    public void onPictureTaken(byte[] data, Camera camera) {

//	    	String path = Environment.getExternalStorageDirectory().toString() + "/safeguard";
//            File myNewFolder = new File(path);
//            
//            if(!myNewFolder.exists()) {
//            	
//            	myNewFolder.mkdir();
//            }
//            
//            path += "/pictures";
//            myNewFolder = new File(path);
//            if(!myNewFolder.exists()) {
//            	
//            	myNewFolder.mkdir();
//            }
//	    	
//	    	Log.d("CAMERA", "foto tirada!");
//	    	
//	    	String picName = "pic_" + String.valueOf( System.currentTimeMillis() ) + ".jpg";
//	    	File picture = new File( path, picName);
//	    	Uri uriTarget = Uri.fromFile( picture );
//	        OutputStream imageFileOS;
//
//	        try {
//
//	            imageFileOS = getContentResolver().openOutputStream(uriTarget);
//	            imageFileOS.write(data);
//	            imageFileOS.flush();
//	            imageFileOS.close();
//	            
//	            Toast.makeText(getApplicationContext(), "Image saved: " + uriTarget.toString(), Toast.LENGTH_LONG).show();
//	        }
//	        catch (FileNotFoundException e) {
//	        	
//	            e.printStackTrace();
//	        } 
//	        catch (IOException e) {
//	        	
//	            e.printStackTrace();
//	        }
//	        
//	        Log.d("CAMERA", "foto guardada!");
//	        
//	        camera.release();
//	        
//	        FTPUtils.uploadFileToServer(picture, picName, FileType.PICTURE);
//	        
//	        onBackPressed();
	    }
	};

}
