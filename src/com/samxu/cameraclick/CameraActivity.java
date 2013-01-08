package com.samxu.cameraclick;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {

	private static Camera mSingletonCamera = null;
	private static final String TAG = "myCameraTag";
	
	private CameraPreview mCameraPreview = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		
		mCameraPreview = new CameraPreview(CameraActivity.this);
		FrameLayout preview = (FrameLayout) findViewById(R.id.preview);
		preview.addView(mCameraPreview);
		
		initCamera();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_camera, menu);
		return true;
	}
	
    @Override
    public void onDestroy() {
    	releaseCamera();
    	Log.d("CAMERA","Destroy");
        super.onDestroy();
    }
    
    @Override
    public void onPause() {
    	releaseCamera();
    	super.onPause();
    }
	
    private void releaseCamera() {
    	if(mSingletonCamera != null) {
    		mSingletonCamera.stopPreview();
    		mSingletonCamera.release();
    		mSingletonCamera = null;
    	}
    }
    
    @SuppressLint("NewApi")
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
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
        
        Log.d(TAG, "set Orientation: "+result);
        camera.setDisplayOrientation(result);
    }
    
    public static void setEasyOrientation(Camera camera, int degree) {
    	camera.setDisplayOrientation(degree);
    	Log.d(TAG, "set easy Orientation: "+degree);
    }
    
    private void initCamera() {
    	
    	if (checkCameraHardware(CameraActivity.this)) {
			Camera camera = getCameraInstance();
			
			// set Camera parameters
            Camera.Parameters params = camera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            camera.setParameters(params);

			//camera.setDisplayOrientation(-90);
			if( camera != null && mCameraPreview != null ) {
				mCameraPreview.setCamera(camera);
				Log.e(TAG, "create preview ...");
			}
    	}
    }
    
    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

	@SuppressLint("NewApi")
	public static Camera getCameraInstance(){
        if(mSingletonCamera == null) {
	        try {
	        	mSingletonCamera = Camera.open(); // attempt to get a Camera instance
	        	if (mSingletonCamera == null ) {
	        		Log.d(TAG, "front camera");
	        		try {
	        			mSingletonCamera = Camera.open(Camera.getNumberOfCameras ()-1);
					} catch (Exception e) {
						// TODO: handle exception
						Log.e(TAG,e.getMessage());
					}
	        	}
	        }
	        catch (Exception e){
	            // Camera is not available (in use or does not exist)
	        	Log.e(TAG,e.getMessage());
	        }
        }
        return mSingletonCamera; // returns null if camera is unavailable
    }
	
	
	private ShutterCallback mShutter = new ShutterCallback() {
	    @Override
	    public void onShutter() {
	        //Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show();
	    }
	    
	};
	
    private PictureCallback mPicture = new PictureCallback() {

        @SuppressWarnings("unused")
		@Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = null;//getOutputMediaFile(MEDIA_TYPE_IMAGE);

			String filename = FileUtility.generateFilename();
			String fullpathString = FileUtility.getFileUtility().getFileDir().getPath() + File.separator + filename;
			pictureFile = new File(fullpathString);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: no file");
            } else {
            
	            if(false){// produce the rgb image
	    			Camera.Parameters params = camera.getParameters();
	    			int imageWidth = params.getPreviewSize().width;
	    			int imageHeight = params.getPreviewSize().height;
	    			int[] rgbData = new int[imageWidth * imageHeight]; 
	    			dataToRGB(rgbData, data, imageWidth, imageHeight);  
	    		}
	
	            try {
	                FileOutputStream fos = new FileOutputStream(pictureFile);
	                fos.write(data);
	                fos.close();
	                Toast.makeText(CameraActivity.this, "The Image saved:" + filename, Toast.LENGTH_LONG).show();
	            } catch (FileNotFoundException e) {
	                Log.d(TAG, "File not found: " + e.getMessage());
	            } catch (IOException e) {
	                Log.d(TAG, "Error accessing file: " + e.getMessage());
	            }
            }

            resetCamera();
            
        }
    };
    
    public void resetCamera() {
        Camera camera = getCameraInstance();
        try {
        	camera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // start preview with new settings
        try {
        	camera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
    
    
	public void onSnapClick(View v) {
		if( getCameraInstance() != null ) {
			getCameraInstance().takePicture(mShutter, null, null, mPicture);
		}
	}
	
    public void onCancelClick(View v) {
    	releaseCamera();
        finish();
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        @SuppressWarnings("deprecation")
		public CameraPreview(Context context) {
        	
            super(context);

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        
        public void setCamera(Camera camera) {
        	mCamera = camera;
        	
        	try {
        		mCamera.setPreviewDisplay(mHolder);
        		//setEasyOrientation(mCamera, 90);
        		mCamera.startPreview();
                
        		//Log
        		Log.e(TAG,"set Camera...");
        	}catch (Exception e) {
        		Log.e(TAG, "Error setting camera preview: " + e.getMessage());
			}
        }

        public void surfaceCreated(SurfaceHolder holder) {
        	if( mCamera == null )
        	{
        		return;
        	}
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                //setCameraDisplayOrientation(CameraActivity.this, 0, mCamera);
                mCamera.startPreview();
                Log.e(TAG,"surface_created.....");
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
              // preview surface does not exist
              return;
            }
            
            if( mCamera == null )
        	{
        		return;
        	}

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
              // ignore: tried to stop a non-existent preview
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                setEasyOrientation(mCamera, 90);
                mCamera.startPreview();
                Log.d(TAG, "surface changed");
            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
    
    
    static public void dataToRGB(int[] rgb, byte[] data, int width, int height) {
    	final int frameSize = width * height;
    	
    	for (int j = 0, yp = 0; j < height; j++) {
    		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
    		for (int i = 0; i < width; i++, yp++) {
    			int y = (0xff & ((int) data[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & data[uvp++]) - 128;
    				u = (0xff & data[uvp++]) - 128;
    			}
    			
    			int y1192 = 1192 * y;
    			int r = (y1192 + 1634 * v);
    			int g = (y1192 - 833 * v - 400 * u);
    			int b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }

}
