package com.MSRi.ivr;

import java.io.File;   
import java.io.FileWriter;
import java.io.IOException; 

import android.util.Log;

/** 
 * 
 * */
public class SaveUserLogs {
	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;

	/** Folder containing all audio files that have yet to be sent. */
	private final String mInnerDir = "/ToBeSent";

	private String mPhotoFile = "";

	private String mAudioPath;
	
	private String mPhoneNumber = "";
	
	public SaveUserLogs(String mainDir, String audioRecordingPath) { 
		mMainDir = mainDir;
		mAudioPath = audioRecordingPath;
		
		// This folder will be queried when there's Internet - files that 
		// need to be sent should be stored in here 
		File dirInner = new File(mMainDir + "/Logs");
		if(!dirInner.exists() || !dirInner.isDirectory()) {
			dirInner.mkdir();
		} 
	} 

	public void setPhotoPath(String path) { 
		mPhotoFile = path;
	}
	
	public void setPhoneNumber(String phone) { 
		mPhoneNumber = phone;
	}


	public void writeToFile() {  
		Log.e("!!", "in the function");
		
		String content = mAudioPath + "," + mPhotoFile + "," + mPhoneNumber; 
		Log.e("!!!", content);
		File root = new File(mMainDir + "/Logs");
		try { 
			if (!root.exists()) {
				root.mkdirs();
			}
			File gpxfile = new File(root, mAudioPath + ".txt");
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(content);
			writer.flush();
			writer.close(); 
		} catch (IOException e) { 
			e.printStackTrace();
		} catch(Exception e) { 
			e.printStackTrace();
		}
	} 	
}
