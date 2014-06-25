package com.MSRi.ivr;

import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final String TAG = "MainActivity" ;
	private Timer timer;
	private PlayAudio playAudio;
	private MediaPlayer mp;
	
	private Button one;
	private Button two;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        
        if(timer != null){
            timer.cancel();
        }
        
        timer = new Timer();
        playAudio = new PlayAudio();
        
    	mp = MediaPlayer.create(MainActivity.this, R.raw.welcome);
    	mp.start(); 
    	
        one.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				mp.release();
				timer.cancel();
				recordInput();
			}  
        }); 
        
        two.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg) {
				mp.release();
				timer.cancel();
				loadRecordings();
			}  
        });  
    }
    
 
    
    private void recordInput() {
    	Intent intent = new Intent(this, RecordAudio.class);
    	startActivity(intent);
    }
    
    private void loadRecordings() {
    	Log.e(TAG, "loadRecordings");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();   
        playAudio = new PlayAudio();
        if(timer != null){
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(playAudio, 3000, 10000); 
    }
    
    @Override
    protected void onPause(){
        super.onPause(); 
        playAudio.stop();
        timer.cancel(); 
    }
    
    class PlayAudio extends TimerTask {
    	MediaPlayer mp;
    	
    	@Override
    	public void run() {
    		Log.e(TAG, "called");
	    	setVolumeControlStream(AudioManager.STREAM_MUSIC);
	    	mp = MediaPlayer.create(MainActivity.this, R.raw.welcome);
	    	mp = MediaPlayer.create(MainActivity.this, R.raw.record_1_listen_2);
	    	mp.start();
	    }
    	
    	public void stop() { 
    		if(mp != null) { 
    			mp.release();
    		}
    	}
    } 
}
