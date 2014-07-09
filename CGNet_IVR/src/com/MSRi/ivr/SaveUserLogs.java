package com.MSRi.ivr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class SaveUserLogs {

	
	public SaveUserLogs(String mainDir, String audioRecordingPath) { 
		// This folder will be queried when there's Internet - files that 
		// need to be sent should be stored in here 
		File dirInner = new File(mainDir + "/Logs");
		if(!dirInner.exists() || !dirInner.isDirectory()) {
			dirInner.mkdir();
		} 
		
		File gpxfile = new File(mainDir, mainDir + "/Logs" + audioRecordingPath + ".txt");
        FileWriter writer;
		try {
			writer = new FileWriter(gpxfile);
	        writer.append("");
	        writer.flush();
	        writer.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}

	}
	
//	FileOutputStream fOut = openFileOutput("savedData.txt",  MODE_APPEND);
	
	
	
}
