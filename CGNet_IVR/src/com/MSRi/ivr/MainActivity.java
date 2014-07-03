package com.MSRi.ivr;

import java.io.File;
import java.util.Timer;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import java.util.TimerTask; 
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.os.Environment; 
import android.media.MediaPlayer;
import android.media.AudioManager;
import android.view.View.OnClickListener;

/** This is the first screen of the CGNet Swara App. 
 *  It allows the user to either record a message (which is then sent to a central location) 
 *  or listen to recordings.
 *  @author Krittika D'Silva (krittika.dsilva@gmail.com) */ 
public class MainActivity extends Activity {
	protected static final String TAG = "MainActivity";
	
	/** Calls the PlayAudio class at a set interval. */
	private Timer timer;
	
	/** Plays a recording instructing the user.  */
	private PlayAudio playAudio;
	
	/** Used to play the audio on the device. */
	private MediaPlayer mp;
	
	/** Opens an activity that allows a user to record and send a message. */
	private Button one;
	
	/** Opens an activity that allows a user to listen to recordings. */
	private Button two;
	
	/** Called when the activity is first created. */
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
            	
        one.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				stopPlayingAudio(mp); 
				timer.cancel();
				recordInput();
			}  
        }); 
        
        two.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg) {
				stopPlayingAudio(mp); 
				timer.cancel();
				loadRecordings();
			}  
        });
        
        // Creates a folder for the app's recordings
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path += "/CGNetSwara";
        File dir = new File(path); 
        if (!dir.exists()|| !dir.isDirectory()) {
            dir.mkdirs();
        }
    }
    
    /** Releases resources back to the system.  */
	private void stopPlayingAudio(MediaPlayer mp) {
		if(mp != null) {
			mp.release();   
			mp = null;	
		}
	}

	/** Called when the activity is paused; begins playing the audio recording
	 *  for the user. */
    @Override
    public void onResume() {
        super.onResume();   
        playAudio = new PlayAudio();
    	mp = MediaPlayer.create(MainActivity.this, R.raw.welcome);
    	mp.start(); 
    	
        if(timer != null){
            timer.cancel();
        }
        
        timer = new Timer();
        timer.schedule(playAudio, 3000, 10000); 
    }
    
    /** Called when the activity is paused; releases resources back to the 
     *  system and stops audio recordings that may be playing. */
    @Override
    protected void onPause() {
        super.onPause();  
        stopPlayingAudio(mp);   
        playAudio.stop();
        timer.cancel(); 
    }
    
    /** Opens a new activity to allow the user to record audio content. */
    private void recordInput() {
    	Intent intent = new Intent(this, RecordAudio.class);
    	startActivity(intent);
    }
    
    /** Opens a new activity to allow the user to view and listen to 
     *  recordings. */
    private void loadRecordings() { 
    	Intent intent = new Intent(this, GetAudioFiles.class);
    	startActivity(intent);
    }

    /** Inflates the menu. Currently, there aren't any meaningful items
     *  to add to the action bar. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    /** Plays the introductory audio instructing users to press either 1 or 2. */
    class PlayAudio extends TimerTask {
    	/** Android's audio player */
    	MediaPlayer mp;
    	
    	/** Streams the audio recording. */
    	@Override
    	public void run() { 
	    	setVolumeControlStream(AudioManager.STREAM_MUSIC); 
	    	mp = MediaPlayer.create(MainActivity.this, R.raw.record_1_listen_2);
	    	mp.start();
	    }
    	
    	/** Stops the audio recording from streaming. */ 
    	public void stop() { 
    		if(mp != null) { 
    			stopPlayingAudio(mp);  
    		}
    	}
    } 
}
