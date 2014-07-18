package com.MSRi.ivr;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class GetAudioFiles extends Activity {
	private static final String TAG = "GetAudioFiles";

	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_audio); 
		
		// Create folders for the audio files 
		setupDirectory();
	}


	/** Sets up file structure for audio recordings. **/
	private void setupDirectory() {
		// This folder should have been created in MainActivity
		// This is just in case it wasn't.
		mMainDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		mMainDir += "/CGNetSwaraDownloads";
		File dir = new File(mMainDir);
		if(!dir.exists() || !dir.isDirectory()) {
			dir.mkdir();
		}  
	} 
}