package com.MSRi.ivr;

import java.io.File; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
 
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
		
		File file = new File(mainDir, mainDir + "/Logs" + audioRecordingPath + ".txt");
		
		
	    try {
	    	FileOutputStream fOut = new FileOutputStream(file);
		    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
	    	myOutWriter.append(audioRecordingPath);
	    	 myOutWriter.close();
	 	    fOut.close();
	 		   
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
