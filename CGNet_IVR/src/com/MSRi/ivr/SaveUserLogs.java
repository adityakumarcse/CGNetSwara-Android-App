package com.MSRi.ivr;

import java.io.File;   
import java.io.FileWriter;
import java.io.IOException; 

import android.util.Log;


public class SaveUserLogs {
	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Folder containing all audio files that have yet to be sent. */
	private final String mInnerDir = "/ToBeSent";
	 
	private String mPhotoFile;
	
	public SaveUserLogs(String mainDir, String audioRecordingPath) { 
		Log.e("Save user logs", "ASDASD");
		mMainDir = mainDir;
		// This folder will be queried when there's Internet - files that 
		// need to be sent should be stored in here 
		File dirInner = new File(mainDir + "/Logs");
		if(!dirInner.exists() || !dirInner.isDirectory()) {
			dirInner.mkdir();
		} 
		
		
		File root = new File(mainDir + "/Logs");
		 
	    try { 
	    	if (!root.exists()) {
	            root.mkdirs();
	        }
	        File gpxfile = new File(root, audioRecordingPath + ".txt");
	        FileWriter writer = new FileWriter(gpxfile);
	        writer.append(audioRecordingPath);
	        writer.flush();
	        writer.close();
	         
	 		   
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch(Exception e) { 
			
		}
	   
	} 
	
	public void setPhotoPath(String path) { 
//		FileOutputStream fOut = openFileOutput("savedData.txt",  MODE_APPEND);
	// check both folders
	}
	
	
	
	
}
