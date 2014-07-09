package com.MSRi.ivr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity; 

public class GetAudioFiles extends Activity {
	private static final String TAG = "GetAudioFiles";
	  
	/** CGNet Swara's main directory with audio files. */
	private String mMainDir;
	
	/** Starts recording audio.  */
	private Button mDownloadMore;
	
	private String mDropboxURL = "https://www.dropbox.com/sh/f88nptylz1jjbem/AACWoQays-JWm_BSKIuVNAHoa";
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_audio); 
        
        mDownloadMore = (Button) findViewById(R.id.download_more);
        
		// Create folders for the audio files 
		//setupDirectory();
        
		// Check
        populateListView();
        
        mDownloadMore.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 		
				downloadFiles();
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
    	
	}
    
    
    private void downloadFiles() { 
    	//HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    	
    	
    	
    	
/*        
    	if(ISSUE_DOWNLOAD_STATUS.intValue()==ECMConstant.ECM_DOWNLOADING){
            File file=new File(DESTINATION_PATH);
            if(file.exists()){
                 downloaded = (int) file.length();
                 connection.setRequestProperty("Range", "bytes="+(file.length())+"-");
            }
        } else{
            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);
        progressBar.setMax(connection.getContentLength());
         in = new BufferedInputStream(connection.getInputStream());
         fos=(downloaded==0)? new FileOutputStream(DESTINATION_PATH): new FileOutputStream(DESTINATION_PATH,true);
         bout = new BufferedOutputStream(fos, 1024);
        byte[] data = new byte[1024];
        int x = 0;
        while ((x = in.read(data, 0, 1024)) >= 0) {
            bout.write(data, 0, x);
             downloaded += x;
             progressBar.setProgress(downloaded);
        } */
    	
    }


	/** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }    
}