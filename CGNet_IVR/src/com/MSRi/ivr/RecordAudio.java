package com.MSRi.ivr;
 
import java.io.IOException;

import android.app.Activity; 
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle; 
import android.os.Environment;
import android.util.Log;
import android.view.Menu; 
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordAudio extends Activity {
    private static final String TAG = "RecordAudio";
 
	private MediaPlayer  mPlayer = null;
	private static String mFileName = null;
	private MediaRecorder mRecorder = null;
	
    private Button start;
	private Button stop;
	private Button playback;

	MediaPlayer mp;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.record_audio); 

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        playback = (Button) findViewById(R.id.playback);
        
        start.setEnabled(true);
        stop.setEnabled(false);
        playback.setEnabled(false);
        
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/AudioRecording.3gp"; // what file extension (?)   
        Log.e(TAG, mFileName);
        
        playAudio();
        start.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) { 		
				stop.setEnabled(true);
				start.setEnabled(false);
				
				startRecording();
			}  
        }); 
        
        
        stop.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				stop.setEnabled(false); 
				playback.setEnabled(true);
				
				stopRecording();
			}  
        });
        
        playback.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg) {
				stop.setEnabled(false);
				start.setEnabled(false);
				playback.setEnabled(false);
				startPlaying();
			}
        });
	}
	
	private void playAudio() { 
    	mp = MediaPlayer.create(this, R.raw.mistake_0_beep_start_finish_1);
    	mp.start(); 
	}
	
	
 
	private void stopRecording() { 
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
	}
	
    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
	
	
	private void startRecording() { 
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
            Log.e(TAG, "recording starting");
            mRecorder.start();

        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        } 
	}

	
    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }		
    }
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	} 
}