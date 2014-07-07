package com.MSRi.ivr;

import android.os.Bundle;
import android.view.Menu;
import android.app.Activity; 

public class GetAudioFiles extends Activity {
//	private static final String TAG = "RecordAudio";
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_audio); 
    }
 

    /** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }    
}