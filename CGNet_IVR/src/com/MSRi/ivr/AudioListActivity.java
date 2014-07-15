package com.MSRi.ivr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AudioListActivity extends ListActivity {
	String TAG = "AudioListActivity";
	
	// Songs list
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
 
    private String mMainDir;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_audio);
 
		mMainDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		mMainDir += "/CGNetSwaraDownloads";
  
        File dir = new File(mMainDir);
        File[] filelist = dir.listFiles();
        String[] theNamesOfFiles = new String[filelist.length];
        for (int i = 0; i < theNamesOfFiles.length; i++) {
           theNamesOfFiles[i] = filelist[i].getName();
           Log.e(TAG, " " + filelist[i].getName());
        }
         
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        					 R.layout.playlist_item, theNamesOfFiles);
        
       // selecting single ListView item
        ListView lv = (ListView) findViewById(R.id.list);
        
        lv.setAdapter(adapter);
        	
        lv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 //play file
            }
        });
    }

}
