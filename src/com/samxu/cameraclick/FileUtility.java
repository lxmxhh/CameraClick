package com.samxu.cameraclick;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

public class FileUtility {
	private static FileUtility mFileUtility = null;
	protected static final String PIC_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "";
	private static final String PICTUREFILEDIR_STRING = "my_folder";
	private static final String TAG = "SAM_FILEUTIL";
	
	private static File picFileDir = null;
	
	private FileUtility() {
		initFileDir();
	}
	
	public static FileUtility getFileUtility() {
		if (mFileUtility == null) {
			mFileUtility = new FileUtility();
		}
		
		return mFileUtility;
	}
	
	
	
	public void initFileDir() {
		if(isExternalStorageWritable() && isExternalStorageReadable()){
			picFileDir = new File(PIC_DIR, PICTUREFILEDIR_STRING);
	        if (!picFileDir.exists()){
	        	if (!picFileDir.mkdirs()) {
		            Log.e(TAG, "Failed to creat the picture directory!");
	        	}
	        }
	    }else{
	    	Log.e(TAG, "no exteranl storage.");
	    }
	}
	
	
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
            Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    
    
    public File getFileDir() {
    	return picFileDir;
    }
    
    
    @SuppressLint("SimpleDateFormat")
	public static String generateFilename() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
		String date = dateFormat.format(new Date());
		String photoFile = "Picture_" + date + ".jpg";

		return photoFile;
    }

}
