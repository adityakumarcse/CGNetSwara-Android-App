package com.MSRi.ivr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream; 
import java.net.HttpURLConnection; 
import java.net.URL;  
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener; 
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar; 
import android.app.Activity;  

public class GetAudioFiles extends Activity {
	private static final String TAG = "GetAudioFiles";
	  
	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Starts recording audio.  */
	private Button mDownloadMore;
	
	/** */
	ProgressBar progressBar;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_audio); 
		progressBar = (ProgressBar) findViewById(R.id.progressbar);
		progressBar.setProgress(0);
		progressBar.setVisibility(View.VISIBLE);
		progressBar.setIndeterminate(false);

		
        mDownloadMore = (Button) findViewById(R.id.download_more);
        
		// Create folders for the audio files 
		setupDirectory();
        
		// 
        populateListView();
      //  progressBar.setProgress(100);
        mDownloadMore.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 		
				downloadFiles();
				Log.e(TAG, "CLICKED!");
			}  
		}); 
        
        
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
 

    private void populateListView() {
    	File dir = new File(mMainDir);
    	Log.e(TAG, "" + mMainDir);
        File[] filelist = dir.listFiles();

    	Log.e(TAG, "" + filelist);
    	
        ListItem[] theNamesOfFiles = new ListItem[filelist.length];
        Log.e(TAG, "length: " + filelist.length); 
        
        for (int i = 0; i < theNamesOfFiles.length; i++) {
           theNamesOfFiles[i] = new ListItem(filelist[i].getName(), filelist[i].getAbsolutePath());
           Log.e(TAG, " " + filelist[i].getName());
        }
        
        
        ArrayAdapterItem adapter = new ArrayAdapterItem(this, 
				 R.layout.playlist_item, theNamesOfFiles);

        ListView listViewItems = (ListView) findViewById(android.R.id.list); 
        listViewItems.setAdapter(adapter);  
	}
    

	
    private void downloadFiles() { 
    	new DownloadAudioFile().execute(); 
    }


	/** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }   
    
    
    
    public class DownloadAudioFile extends AsyncTask<Void, Integer, Void> {
    	private static final String TAG = "DownloadAudioFile";
    	 
        protected Void doInBackground(Void... urls) {
        	 
        	String mMainDir = Environment.getExternalStorageDirectory().getAbsolutePath();
    		mMainDir += "/CGNetSwaraDownloads";
        	String path = mMainDir + "/test10.wav";
        	
        	int count = 0;
        	 
         	Log.e(TAG,"?");
    		URL url;
    		try {
    			url = new URL("http://cgnetswara.org/sounds/270.wav");
    			
    	    	
    		  	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	       	int downloaded = 0;
    			if(true){
    	            File file=new File(path);
    	            if(!file.exists()) {
    	            	file.createNewFile();
    	            } 
    	            if(file.exists()){ 
    	                downloaded = (int) file.length(); 
    	                connection.setRequestProperty("Range", "bytes="+(file.length())+"-");
    	            }
    	        } //else{
    	           // connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
    	        //}
    	        connection.setDoInput(true);
    	        connection.setDoOutput(true);
    	        progressBar.setMax(connection.getContentLength()); 
    			 
    			BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
    	        FileOutputStream fos = (downloaded==0)? new FileOutputStream(path): new FileOutputStream(path,true);
    	        BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
    	        byte[] data = new byte[1024];
    	        int x = 0;

    	        while ((x = in.read(data, 0, 1024)) >= 0) { 
    	            bout.write(data, 0, x);
    	            downloaded += x;   
    	            publishProgress(downloaded);
    	        }
    	        bout.close();
    		} catch(Exception e) { 
    			e.printStackTrace();
    		}
    		
    		return null; 
        }
        
        @Override
        protected void onProgressUpdate(Integer... values) {
        	final ProgressBar bar = progressBar;
        	final int size = values[0];
			runOnUiThread(new Runnable() {    
				@Override
				public void run() { 
					bar.setProgress(size);  
				} 
			});  
        }  
    }  
}